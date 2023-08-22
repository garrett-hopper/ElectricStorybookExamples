(ns user
  (:require
   [app.server :refer [start-server!]]
   [shadow.cljs.devtools.server :refer [start!] :rename {start! shadow-start!}]
   [shadow.cljs.devtools.api :refer [watch] :rename {watch shadow-watch}]))

(def electric-server-config
  {:host "0.0.0.0"
   :port 8080
   :resources-path "public"})

(def server (atom nil))

(defn main [& _]
  (println "Starting Electric compiler and server...")
  (shadow-start!)
  (shadow-watch :books)
  (when @server
    (println "Stopping server...")
    (.stop @server))
  (println "Starting server...")
  (reset! server (start-server! electric-server-config)))

(comment (main))
