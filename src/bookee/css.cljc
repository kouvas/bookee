(ns ^{:ornament/prefix "bookee__"} bookee.css
  (:require [lambdaisland.ornament :as o])
  #?(:cljs (:require-macros bookee.css)))

(def global-styles
  [[:* {:margin     0
        :padding    0
        :box-sizing "border-box"}]
   [:html {:scroll-behavior "smooth"}]
   [:body {:height           "100%"
           :background-color "#ececec"}]])

(o/defstyled content :div
  {})

(o/defstyled navbar :nav
  {:background-color "white"
   :box-shadow       "3px 3px 5px rgb(0, 0, 0, 0.1)"
   :top              "0"
   :width            "100%"
   :position         "sticky"
   :z-index          "1000"}
  [:ul {:display         "flex"
        :width           "100%"
        :margin-left     ".5rem"
        :margin-bottom   "1.5rem"
        :scrollbar-width "none"
        :list-style      "none"
        :justify-content "flex-start"
        :align-items     "center"
        :overflow-x      "auto"}]
  [:li {:height "50px"}]
  [:a {:height          "100%"
       :padding         "0 0.3rem"
       :text-decoration "none"
       :display         "flex"
       :align-items     "center"
       :color           "black"}
   [:&:hover {:background-color "#f0f0f0f0"}]])

(o/defstyled main :main
  [:& {:display         "flex"
       :flex-direction  "column"
       :justify-content "center"
       :align-items     "center"
       :width           "100%"
       ; Padding on all sides for consistent spacing
       :padding         "1rem"}])
(o/defstyled container :div
  [:& {:background-color "#ffffff"
       :width            "100%"
       :max-width        "896px"
       :min-height       "90vh"
       :box-shadow       "0 4px 12px rgba(0,0,0,0.1);"
       ; Remove margin since main has padding
       :margin           "0"
       :padding          "1rem"
       :border-radius    "0.8rem"
       :box-sizing       "border-box"}]
  [:section {:margin-bottom "1rem"}])

(o/defstyled footer :footer
  {:background-color "#e06363"
   :color            "white"}
  [:h2 {:padding "5rem"}])

;; Base card component (reusable, can be inherited, generates a css class)
(o/defstyled base-card :div
  {:background-color "#ffffff"
   :border-radius    "0.8rem"
   :box-shadow       "0 2px 8px rgba(0,0,0,0.1)"
   :padding          "1rem"
   :margin           "0.5rem"
   :transition       "all 0.3s ease"
   :cursor           "pointer"}
  [:&:hover {:transform  "translateY(-2px)"
             :box-shadow "0 4px 16px rgba(0,0,0,0.15)"}])

(o/defstyled service-card base-card
  ;; Extends base-card, only add/override specific styles
  {:padding "1.5rem"
   :margin  "1rem"}
  [:.service-name {}]
  [:.service-details {}]
  [:.price {}]
  [:.duration {}])

(o/defstyled team-card base-card
  {:display     "flex"
   :align-items "center"
   :gap         "1rem"
   :min-height  "80px"}
  [:.team-image {:width         "60px"
                 :height        "60px"
                 :border-radius "50%"
                 :object-fit    "cover"
                 :flex-shrink   "0"}]
  [:.team-content {:flex-grow      "1"
                   :display        "flex"
                   :flex-direction "column"
                   :gap            "0.25rem"}]
  [:.team-name {:font-size   "1.125rem"
                :font-weight "600"
                :color       "#1f2937"}]

  [:.team-role {:font-size "0.875rem"
                :color     "#6b7280"}]

  [:.team-specialties {:display    "flex"
                       :flex-wrap  "wrap"
                       :gap        "0.25rem"
                       :margin-top "0.5rem"}]

  [:.specialty-badge {:background-color "#f3f4f6"
                      :color            "#374151"
                      :padding          "0.125rem 0.5rem"
                      :border-radius    "0.375rem"
                      :font-size        "0.75rem"}]

  [:.team-arrow {:color       "#9ca3af"
                 :flex-shrink "0"
                 :transition  "transform 0.2s ease"}]

  [:&:hover [:.team-arrow {:transform "translateX(4px)"
                           :color     "#4b5563"}]])

(o/defstyled details-link :a
  {:color           "#0077cc"                          ; base color
   :text-decoration "none"                             ; remove underline
   :font-weight     "500"
   :position        "relative"
   :transition      "color 0.2s ease"}

  [:&:after {:content          "\"\""
             :position         "absolute"
             :left             "0"
             :bottom           "-2px"
             :width            "100%"
             :height           "2px"
             :background       "#0077cc"
             :transform        "scaleX(0)"
             :transform-origin "right"
             :transition       "transform 0.3s ease"}]

  [:&:hover {:color "#005fa3"}]                        ; darker on hover

  [:&:hover:after {:transform        "scaleX(1)"
                   :transform-origin "left"}])

(o/defstyled details-body :div
  [:& {:padding ".25rem"}])