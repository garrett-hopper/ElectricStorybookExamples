(ns utils.storybook)

(defmacro defbook [book-name & body]
  (when (:ns &env)
    (let [[bindings body] (if (vector? (first body))
                            [(first body) (rest body)]
                            [[]           body])
          args            (gensym "args")]
      (assert (even? (count bindings)) "Bindings must be even")
      `(def ~(with-meta (symbol (str "book-" book-name)) {:export true})
         (cljs.core/js-obj
          "render"
          (fn [~args]
            (let [node# (js/document.createElement "div")]
              (binding [hyperfiddle.electric-client/*ws-server-url* "ws://localhost:8080"]
                ((hyperfiddle.electric/boot
                   (binding [hyperfiddle.electric-dom2/node node#]
                     (let [~@(->> bindings
                                  (partition 2)
                                  (mapcat
                                   (fn [[k v]]
                                     [k `(get ~args ~(keyword k))]))
                                  (concat `(~args (cljs.core/js->clj ~args :keywordize-keys true))))]
                       ~@body)))))
              node#))
          ;; https://storybook.js.org/docs/web-components/writing-stories/parameters
          "parameters"
          (cljs.core/js-obj
           ~@(when-let [parameters (not-empty (meta book-name))]
               (mapcat
                (fn [[k v]]
                  [(name k) `(cljs.core/clj->js ~v)])
                parameters)))
          ;; https://storybook.js.org/docs/web-components/writing-stories/args)
          "args"
          (cljs.core/js-obj
           ~@(->> bindings
                  (partition 2)
                  (mapcat
                   (fn [[k v]]
                     (when (not= v '_)
                       [(str k) `(cljs.core/clj->js ~v)])))))
          ;; https://storybook.js.org/docs/web-components/api/arg-types
          "argTypes"
          (cljs.core/js-obj
           ~@(->> bindings
                  (partition 2)
                  (mapcat
                   (fn [[k v]]
                     (when-let [arg-types (not-empty (meta k))]
                       [(str k) `(cljs.core/clj->js ~arg-types)]))))))))))
