(ns ^{:ornament/prefix "bookee__"} bookee.css
  (:require [lambdaisland.ornament :as o])
  #?(:cljs (:require-macros bookee.css)))

(def breakpoints
  {:xs "320px"
   :sm "480px"
   :md "768px"
   :lg "992px"
   :xl "1200"})

(def palette
  {:primary "white"
   :secondary ""
   :success "#16a34a"
   :success-hover "#f0fdf4"
   :danger "#dc2626"
   :danger-hover "#ffe7e7"
   :warning ""
   :info "#0077cc"
   :info-hover "#005897"

   ;; support colors
   :grey-10 "#f9fafb"
   :grey-20 "#ececec"
   :grey-25 "#dadadaf0"
   :grey-30 "#6b7280"

   :star "#fbbf24"})

(def global-styles
  [[:* {:margin 0
        :padding 0
        :box-sizing "border-box"}]
   [:html {:scroll-behavior "smooth"
           :scroll-padding-top "3rem"}] ;; Add padding for sticky navbar
   [:body {:height "100%"
           :background-color (:grey-20 palette)}]])

(o/defstyled content :div
  {})

(o/defstyled navbar :nav
  {:background-color (:primary palette)
   :box-shadow "3px 3px 5px rgb(0, 0, 0, 0.1)"
   :top "0"
   :width "100%"
   :position "sticky"
   :z-index "1000"}
  [:ul {:display "flex"
        :width "100%"
        :padding-left ".5rem"
        :margin-bottom "1.5rem"
        :scrollbar-width "none"
        :list-style "none"
        :justify-content "flex-start"
        :align-items "center"
        :overflow-x "auto"}]
  [:li {:height "50px"}]
  [:a {:height "100%"
       :padding "0 0.3rem"
       :text-decoration "none"
       :display "flex"
       :align-items "center"
       :color "black"}
   [:&:hover {:background-color (:grey-25 palette)}]
   [:&.active {:background-color (:grey-25 palette)
               :font-weight "600"
               :border-bottom "3px solid #333"}]])

(o/defstyled main :main
  [:& {:display "flex"
       :flex-direction "column"
       :justify-content "center"
       :align-items "center"
       :width "100%"
       :padding "1rem"}])

(o/defstyled container :div
  [:& {:background-color (:primary palette)
       :width "100%"
       :max-width "896px"
       :min-height "90vh"
       :box-shadow "0 4px 12px rgba(0,0,0,0.1);"
       ; Remove margin since main has padding
       :margin "0"
       :padding "1rem"
       :border-radius "0.8rem"
       :box-sizing "border-box"}]
  [:section {:margin-bottom "1rem"}])

(o/defstyled shop-banner :div
  [:at-media {:min-width (:lg breakpoints)}
   {:display "none"}]

  {:padding "1.5rem 1rem"
   :border-bottom "1px solid #e5e7eb"
   :margin-bottom "1rem"}

  [:.banner-content {:display "flex"
                     :gap "1rem"
                     :align-items "flex-start"}]
  [:.shop-logo {:width "80px"
                :height "80px"
                :border-radius "0.5rem"
                :object-fit "cover"
                :flex-shrink "0"}]
  [:.shop-info {:flex "1"
                :display "flex"
                :flex-direction "column"
                :gap "0.5rem"}]
  [:.shop-header {:display "flex"
                  :justify-content "space-between"
                  :align-items "flex-start"}]
  [:.shop-name {:font-size "1.25rem"
                :font-weight "600"}]
  [:.rating-line {:display "flex"
                  :align-items "center"
                  :gap "0.5rem"}]
  [:.rating-value {:font-weight "600"}]
  [:.review-count {:color (:grey-30 palette)
                   :font-size "0.875rem"}]
  [:.hours-container {:display "flex"
                      :align-items "center"
                      :gap "0.5rem"}]
  [:.hours-button {:display "flex"
                   :align-items "center"
                   :gap "0.25rem"
                   :padding "0.25rem 0.5rem"
                   :background "transparent"
                   :border "none"
                   :cursor "pointer"
                   :font-size "0.875rem"
                   :border-radius "0.25rem"
                   :transition "background-color 0.2s"}]
  [:.closed {:color (:danger palette)}
   [:&:hover {:background-color (:danger-hover palette)}]]
  [:.open {:color (:success palette)}
   [:&:hover {:background-color (:success-hover palette)}]]

  [:.hours-dropdown {:margin-top "1rem"
                     :padding "1rem"
                     :max-width (:xs breakpoints)
                     :background-color (:grey-10 palette)
                     :border-radius "0.5rem"}]
  [:.hours-title {:font-weight "600"
                  :margin-bottom "0.5rem"}]
  [:.hours-list {:display "flex"
                 :flex-direction "column"
                 :gap "0.25rem"}]
  [:.day-row {:display "flex"
              :justify-content "space-between"
              :padding "0.25rem 0"
              :font-size "0.875rem"}]
  [:.chevron {:transition "transform 0.2s"
              :display "inline-block"}]
  [:.chevron.collapsed {:transform "rotate(-180deg)"}])

;; Base card component (reusable, can be inherited, generates a css class)
(o/defstyled base-card :div
  {:background-color (:primary palette)
   :border-radius "0.8rem"
   :box-shadow "0 2px 8px rgba(0,0,0,0.1)"
   :padding "1rem"
   :margin "0.5rem"
   :transition "all 0.3s ease"
   :cursor "pointer"}
  [:&:hover {:transform "translateY(-2px)"
             :box-shadow "0 4px 16px rgba(0,0,0,0.15)"}])

(o/defstyled service-card base-card
  ;; Extends base-card, only add/override specific styles
  {:padding "1.5rem"
   :margin "1rem"}
  [:.service-name {}]
  [:.service-details {}]
  [:.price {}]
  [:.duration {}])

(o/defstyled team-card base-card
  {:display "flex"
   :align-items "center"
   :gap "1rem"
   :min-height "80px"}
  [:.team-image {:width "60px"
                 :height "60px"
                 :border-radius "50%"
                 :object-fit "cover"
                 :flex-shrink "0"}]
  [:.team-content {:flex-grow "1"
                   :display "flex"
                   :flex-direction "column"
                   :gap "0.25rem"}]
  [:.team-name {:font-size "1.125rem"
                :font-weight "600"}]

  [:.team-role {:font-size "0.875rem"
                :color (:grey-30 palette)}]

  [:.team-specialties {:display "flex"
                       :flex-wrap "wrap"
                       :gap "0.25rem"
                       :margin-top "0.5rem"}]

  [:.specialty-badge {:background-color (:grey-20 palette)
                      :color (:grey-30 palette)
                      :padding "0.125rem 0.5rem"
                      :border-radius "0.375rem"
                      :font-weight "500"
                      :font-size "0.75rem"}]

  [:.team-arrow {:color (:info palette)
                 :flex-shrink "0"
                 :transition "transform 0.2s ease"}]

  [:&:hover [:.team-arrow {:transform "translateX(4px)"
                           :color (:info-hover palette)}]])

(o/defstyled details-link :a
  {:color (:info palette)
   :text-decoration "none"
   :font-weight "500"
   :position "relative"
   :transition "color 0.2s ease"}

  [:&:after {:content "\"\""
             :position "absolute"
             :left "0"
             :bottom "-2px"
             :width "100%"
             :height "2px"
             :background (:info palette)
             :transform "scaleX(0)"
             :transform-origin "right"
             :transition "transform 0.3s ease"}]

  [:&:hover {:color (:info-hover palette)}]

  [:&:hover:after {:transform "scaleX(1)"
                   :transform-origin "left"}])

(o/defstyled details-body :div
  [:& {:padding ".25rem"}])

(o/defstyled map-container
  :div
  {:margin-top "1rem"
   :height "23rem"
   :width "100%"
   :border-radius ".5rem"
   :overflow "hidden"
   :box-shadow "0 2px 8px rgba(0, 0, 0, 0.1)"})

(o/defstyled address-info
  :div
  {:display "flex"
   :align-items "center"
   :gap ".5rem"
   :margin-bottom "1rem"}
  [:p {:margin "0.5rem 0"}])

(o/defstyled reviews-container :div
  [:& {:padding "1rem"
       :max-width "25rem"}])

(o/defstyled rating-summary :div
  {:margin-bottom "2rem"}
  [:.rating-display {:display "flex"
                     :align-items "center"
                     :gap "1rem"
                     :margin-bottom "1rem"}]
  [:.stars {:display "flex"
            :gap "0.125rem"}]
  [:.star {:color (:star palette)}]
  [:.star-empty {:color (:grey-10 palette)}]
  [:.rating-number {:font-size "2rem"
                    :font-weight "700"}]
  [:.rating-count {:color (:grey-30 palette)
                   :font-size "0.875rem"}]
  [:.rating-bars {:display "flex"
                  :flex-direction "column"
                  :gap "0.5rem"}]
  [:.rating-row {:display "flex"
                 :align-items "center"
                 :gap "0.5rem"}]
  [:.rating-label {:display "flex"
                   :align-items "center"
                   :gap "0.25rem"
                   :min-width "2rem"}]
  [:.rating-bar {:flex "1"
                 :height "0.5rem"
                 :background-color (:grey-20 palette)
                 :border-radius "0.25rem"
                 :overflow "hidden"}]
  [:.rating-fill {:height "100%"
                  :background-color (:star palette)
                  :transition "width 0.3s ease"}]
  [:.rating-value {:min-width "2rem"
                   :text-align "right"
                   :color (:grey-30 palette)
                   :font-size "0.875rem"}])

(o/defstyled review-item :div
  {:border-top "1px solid #e5e7eb"
   :padding "1rem 0"}
  [:&:first-child {:border-top "none"}]
  [:.review-header {:display "flex"
                    :justify-content "space-between"
                    :align-items "flex-start"
                    :margin-bottom "0.5rem"}]
  [:.review-author {:display "flex"
                    :flex-direction "column"
                    :gap "0.25rem"}]
  [:.author-name {:font-weight "500"}]
  [:.review-rating {:display "flex"
                    :gap "0.125rem"}]
  [:.review-date {:color (:grey-30 palette)
                  :font-size "0.875rem"}]
  [:.review-text {:font-weight "300"
                  :line-height "1.5"}])

(o/defstyled about-container :div
  {:padding "1rem"
   :max-width "25rem"}
  [:p {:line-height "1.6"
       :margin-bottom "1rem"}]
  [:p:first-of-type {:font-size "1.125rem"
                     :font-weight "500"}])

(o/defstyled back-button :button
  {:padding "0.75rem 1rem"
   :background-color "#333"
   :color "white"
   :border "none"
   :border-radius "0.5rem"
   :font-size "1rem"
   :font-weight "500"
   :cursor "pointer"
   :transition "background-color 0.2s"
   :margin-bottom "1rem"}
  [:&:hover {:background-color "#555"}])

(o/defstyled footer :footer
  {:background-color "black"
   :color "white"}
  [:h2 {:padding "5rem"}])
