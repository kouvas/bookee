(ns bookee.hooks
  (:require [bookee.css :as css]
            [clojure.string :as string]
            [garden.compiler :as gc]
            [lambdaisland.ornament :as o]))

(defn write-styles-hook
  "Shadow-cljs build hook to generate CSS from Ornament styles. See Ornament's docs"
  {:shadow.build/stage :flush}
  [build-state & args]
  ;; Initialize Ornament
  (o/set-tokens! {})

  (require 'bookee.css :reload)

  (let [ornament-css (o/defined-styles)
        global-css   (gc/compile-css css/global-styles)
        css-content  (str global-css "\n" ornament-css)]
    (spit "resources/public/index.css" css-content)
    (println "CSS generated and written to resources/public/index.css")
    (println (str "Generated " (count (string/split css-content #"\n")) " lines of CSS")))

  build-state)