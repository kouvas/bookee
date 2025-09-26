(ns ^{:ornament/prefix "bookee__"} bookee.css
  (:require [lambdaisland.ornament :as o])
  #?(:cljs (:require-macros bookee.css)))

(def global-styles
  [[:* {:margin     0
        :padding    0
        :box-sizing "border-box"}]
   [:html {:scroll-behavior    "smooth"
           :scroll-padding-top "3rem"}]                ;; Add padding for sticky navbar
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
   [:&:hover {:background-color "#f0f0f0f0"}]
   [:&.active {:background-color "#e0e0e0"
               :font-weight      "600"
               :border-bottom    "3px solid #333"}]
   [:&.inactive {}]])

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

(o/defstyled map-container
  :div
  {:margin-top    "1rem"
   :height        "23rem"
   :width         "100%"
   :border-radius ".5rem"
   :overflow      "hidden"
   :box-shadow    "0 2px 8px rgba(0, 0, 0, 0.1)"})

(o/defstyled address-info
  :div
  {:display       "flex"
   :align-items   "center"
   :gap           ".5rem"
   :margin-bottom "1rem"}
  [:p {:margin "0.5rem 0"
       :color  "#374151"}]
  [:p:first-child {:color "#1f2937"}])

(o/defstyled reviews-container :div
  [:& {:padding   "1rem"
       :max-width "25rem"}]
  [:h2 {:font-size     "1.5rem"
        :font-weight   "600"
        :margin-bottom "1.5rem"
        :color         "#1f2937"}])

(o/defstyled rating-summary :div
  {:margin-bottom "2rem"}
  [:.rating-display {:display       "flex"
                     :align-items   "center"
                     :gap           "1rem"
                     :margin-bottom "1rem"}]
  [:.stars {:display "flex"
            :gap     "0.125rem"}]
  [:.star {:color "#fbbf24"}]
  [:.star-empty {:color "#e5e7eb"}]
  [:.rating-number {:font-size   "2rem"
                    :font-weight "700"
                    :color       "#1f2937"}]
  [:.rating-count {:color     "#6b7280"
                   :font-size "0.875rem"}]
  [:.rating-bars {:display        "flex"
                  :flex-direction "column"
                  :gap            "0.5rem"}]
  [:.rating-row {:display     "flex"
                 :align-items "center"
                 :gap         "0.5rem"}]
  [:.rating-label {:display     "flex"
                   :align-items "center"
                   :gap         "0.25rem"
                   :min-width   "2rem"}]
  [:.rating-bar {:flex             "1"
                 :height           "0.5rem"
                 :background-color "#e5e7eb"
                 :border-radius    "0.25rem"
                 :overflow         "hidden"}]
  [:.rating-fill {:height           "100%"
                  :background-color "#fbbf24"
                  :transition       "width 0.3s ease"}]
  [:.rating-value {:min-width  "2rem"
                   :text-align "right"
                   :color      "#6b7280"
                   :font-size  "0.875rem"}])

(o/defstyled review-item :div
  {:border-top "1px solid #e5e7eb"
   :padding    "1rem 0"}
  [:&:first-child {:border-top "none"}]
  [:.review-header {:display         "flex"
                    :justify-content "space-between"
                    :align-items     "flex-start"
                    :margin-bottom   "0.5rem"}]
  [:.review-author {:display        "flex"
                    :flex-direction "column"
                    :gap            "0.25rem"}]
  [:.author-name {:font-weight "600"
                  :color       "#1f2937"}]
  [:.review-rating {:display "flex"
                    :gap     "0.125rem"}]
  [:.review-date {:color     "#6b7280"
                  :font-size "0.875rem"}]
  [:.review-text {:color       "#374151"
                  :line-height "1.5"}])

(o/defstyled about-container :div
  {:padding   "1rem"
   :max-width "25rem"}
  [:h2 {:font-size     "1.5rem"
        :font-weight   "600"
        :margin-bottom "1.5rem"
        :color         "#1f2937"}]
  [:p {:color         "#374151"
       :line-height   "1.6"
       :margin-bottom "1rem"}]
  [:p:first-of-type {:font-size   "1.125rem"
                     :font-weight "500"
                     :color       "#1f2937"}])