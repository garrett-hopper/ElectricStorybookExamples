(ns app.server
  (:require
   [hyperfiddle.electric-jetty-adapter :as adapter]
   [clojure.tools.logging :as log]
   [ring.adapter.jetty9 :as ring]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.util.response :as res]
   app.core)
  (:import
   [java.io IOException]
   [java.net BindException]))

(defn wrap-electric-websocket [next-handler]
  (fn [ring-req]
    (if (ring/ws-upgrade-request? ring-req)
      (let [electric-message-handler (partial adapter/electric-ws-message-handler ring-req)]
        (ring/ws-upgrade-response (adapter/electric-ws-adapter electric-message-handler)))
      (next-handler ring-req))))

(defn not-found-handler [_ring-request]
  (-> (res/not-found "Not found")
      (res/content-type "text/plain")))

(defn http-middleware [resources-path]
  (-> not-found-handler
      (wrap-resource resources-path)
      (wrap-content-type)
      (wrap-electric-websocket)
      (wrap-params)))

(defn start-server! [{:keys [port resources-path] :as config}]
  (try
    (let [server (ring/run-jetty (http-middleware resources-path)
                                 (merge {:port port :join? false} config))
          port   (-> server (.getConnectors) first (.getPort))]
      (println "Server started" (str "http://" (:host config) ":" port))
      server)
    (catch IOException err
      (if (instance? BindException (ex-cause err))
        (do (log/warn "Port" port "is unavailable, retrying with" (inc port))
            (start-server! (update config :port inc)))
        (throw err)))))
