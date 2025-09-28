(ns bookee.ui
  (:require [bookee.css :as css]
            [bookee.components :as comp]
            [bookee.data :as data]
            [bookee.icons :as icons]
            [bookee.navigation :as nav]
            [bookee.map :as m]))

(defn services-view [state]
  (let [nav-state (nav/get-current-state (:nav-wmem state))]
    [:section#services
     (when (nav/can-go-back? nav-state)
       (css/back-button
         {:on {:click [[:navigate/back]]}}
         "← Back"))
     [:h2 "Services"]
     (for [service data/services]
       (comp/service-card service state))]))

(defn team-view [state]
  (let [nav-state (nav/get-current-state (:nav-wmem state))]
    [:section#team
     (when (nav/can-go-back? nav-state)
       (css/back-button
         {:on {:click [[:navigate/back]]}}
         "← Back"))
     [:h2 "Team"]
     (for [team-id data/team-ids
           :let [team-member (first (filter #(= (:id %) team-id) data/users))]]
       (comp/team-card team-member state))]))

(defn calendar-view [state]
  (let [nav-state (nav/get-current-state (:nav-wmem state))]
    [:section#calendar
     (when (nav/can-go-back? nav-state)
       (css/back-button
         {:on {:click [[:navigate/back]]}}
         "← Back"))
     [:h2 "Calendar"]
     [:p "Select a date and time for your appointment"]]))

(defn verification-view [state]
  (let [nav-state (nav/get-current-state (:nav-wmem state))]
    [:section#verification
     (when (nav/can-go-back? nav-state)
       (css/back-button
         {:on {:click [[:navigate/back]]}}
         "← Back"))
     [:h2 "Verification"]
     [:p "Please confirm your booking details"]]))

(defn main-view [state]
  [:<>
   (comp/navbar
     state
     {:replicant/on-mount   [[:ui/run-scroll-observer]]
      :replicant/on-unmount [[:ui/cleanup-scroll-observer]]})
   (css/main
     (css/container
       (comp/shop-banner state data/reviews)
       (services-view state)
       (team-view state)
       [:section#about
        [:h2 "About"]
        (comp/about data/shop-description)]
       [:section#reviews
        [:h2 "Reviews"]
        (comp/reviews-section state data/reviews)]
       [:section#address
        [:h2 "Address"]
        (css/address-info
          icons/map-pin-line-icon
          [:p (:address data/shop-info)])
        (m/leaflet-map state)]))
   (css/footer
     [:h2 "La footer"])])

(def views
  {:main         main-view
   :services     services-view
   :team         team-view
   :calendar     calendar-view
   :verification verification-view})

(defn main [state]
  (let [nav-state    (nav/get-current-state (:nav-wmem state))
        current-view (nav/get-view nav-state)]
    (css/content
      (if-let [view-fn (get views current-view)]
        (view-fn state)
        [:div "View not found: " (str current-view)]))))
