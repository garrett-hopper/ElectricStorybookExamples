(ns app.books
  (:require
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-dom2 :as dom]
   [utils.storybook :refer [defbook]]))

(e/defn Example [x]
  (dom/div
    (dom/text (str "Hello, World! X: " x))))

(defbook StoryName
  []
  (Example. 0))

;; Parameters
;; See https://storybook.js.org/docs/web-components/writing-stories/parameters

(defbook ^{:backgrounds {:values [{:name "red" :value "#f00"}
                                  {:name "green" :value "#0f0"}
                                  {:name "blue" :value "#00f"}]}}
    WithCustomBackground
  []
  (Example. 0))

(defbook ^{:layout "centered"} Centered []
         (Example. 0))

;; Args
;; https://storybook.js.org/docs/web-components/writing-stories/args)

(defbook WithArgs
  [x :default-value]
  (Example. x))

;; ArgTypes
;; https://storybook.js.org/docs/web-components/api/arg-types

(defbook WithArgTypes
  [^{:control "select"
     :options ["A" "B" "C"]} x "A"]
  (Example. x))

(defbook WithAction
  [^{:action "Action Name"} on-click _]
  (dom/div
    (dom/button
      (dom/props {:on-click (e/fn [e] (on-click %))})
      (dom/text "Click me!"))))

(defbook ^{:actions {:argTypesRegex "^on.*"}}
    WithMultipleActions
  [on-click-1 _
   on-click-2 _]
  (dom/div
    (dom/button
      (dom/props {:on-click (e/fn [e] (on-click-1 %))})
      (dom/text "Click me 1!"))
    (dom/button
      (dom/props {:on-click (e/fn [e] (on-click-2 %))})
      (dom/text "Click me 2!"))))
