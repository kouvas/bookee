(ns bookee.ui
  (:require [bookee.css :as css]
            [bookee.components :as comp]
            [bookee.data :as data]
            [bookee.utils :as utils]
            [bookee.icons :as icons]
            [bookee.navigation :as nav]
            [bookee.map :as m]
            [bookee.calendar :as cal]
            [taoensso.telemere :as t]))

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
     [:h2 "Select a Date"]
     (cal/main state)]))

(defn verification-view [state]
  (let [nav-state      (nav/get-current-state (:nav-wmem state))
        customer-name  (get-in state [:verification :customer-name])
        customer-email (get-in state [:verification :customer-email])
        email-valid?   (utils/valid-email? customer-email)
        form-valid?    (and (seq customer-name) email-valid?)]
    (css/verification-container
      (when (nav/can-go-back? nav-state)
        (css/back-button
          {:on {:click [[:navigate/back]]}}
          "← Back"))
      [:div.verification-content
       (comp/booking-summary state)
       (css/form-container
         [:div.form-title "Your Information"]
         [:div.form-field
          [:label.field-label "Name"]
          [:input.field-input
           {:type        "text"
            :placeholder "Jean Dupont"
            :value       (or customer-name "")
            :on          {:input [[:verification/update-name :event.target/value]]}}]]
         [:div.form-field
          [:label.field-label "Email"]
          [:input.field-input
           {:type        "email"
            :class       (when (and (seq customer-email) (not email-valid?)) "invalid")
            :placeholder "jean@example.com"
            :value       (or customer-email "")
            :on          {:input [[:verification/update-email :event.target/value]]}}]
          (when (and (seq customer-email) (not email-valid?))
            [:div.error-message "Please enter a valid email address"])]
         (css/button-group
           [:button.secondary-button
            {:on {:click [[:verification/reset-booking]]}}
            "Start Over"]
           [:button.primary-button
            {:disabled (not form-valid?)
             :on       (when form-valid?
                         {:click [[:verification/confirm-booking]]})}
            "Confirm Booking"]))]
      (comp/confirmation-modal state))))


(defn main-view [state]
  [:div
   (comp/navbar
     state
     {:replicant/on-mount   [[:ui/run-scroll-observer]]
      :replicant/on-unmount [[:ui/cleanup-scroll-observer]]})
   (css/main
     (css/container
       (comp/top-shop-banner state data/reviews)
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
        (m/leaflet-map state)])
     (comp/side-shop-banner state data/reviews))
   (css/footer
     [:h2 "La footer"])])

(defn render-ui [state]
  (let [nav-state    (nav/get-current-state (:nav-wmem state))
        current-view (nav/get-view nav-state)]
    (case current-view
      :main
      (main-view state)

      :services
      (css/view-container
        (services-view state))

      :team
      (css/view-container
        (team-view state))

      :calendar
      (css/view-container
        (calendar-view state))

      :verification
      (css/view-container
        (verification-view state))

      (do
        (t/log! :warn
                {:msg  "Unknown view requested"
                 :view current-view})

        (css/view-container
          [:section
           {:style {:padding "1rem"}}
           (css/back-button
             {:on {:click [[:navigate/back]]}}
             "← Go back")
           [:h2 "Page not found"]
           [:p "The requested view could not be found."]])))))
