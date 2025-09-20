(ns bookee.portfolio
  (:require [portfolio.ui :as ui]
            [replicant.dom :as replicant]
            [bookee.components.card-scenes]))

;; Keep the scenes namespace from being eliminated during compilation
:bookee.components.card-scenes/keep

(replicant/set-dispatch! #(prn %3))

(defonce app
         (ui/start!
           {:config
            {:css-paths   ["index.css"]
             :canvas-path "canvas.html"
             :background/options
             [{:id    :light
               :title "Light"
               :value {:background/background-color "#ffffff"}}
              {:id    :dark
               :title "Dark"
               :value {:background/background-color "#1a1a1a"}}]}}))

(defn main []
  app)