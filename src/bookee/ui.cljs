(ns bookee.ui
  (:require [bookee.css :as css]
            [bookee.components :as comp]
            [bookee.data :as data]
            [bookee.icons :as icons]
            [bookee.map :as m]))

(def shop-name "La BarberShop")

(defn main
  [state]
  (css/content
    (comp/navbar
      state
      {:replicant/on-mount   [[:ui/run-scroll-observer]]
       :replicant/on-unmount [[:ui/cleanup-scroll-observer]]})
    (css/main
      (css/container
        [:section#services
         [:h2 "Services"]
         (for [service data/services]
           (comp/service-card service state))]
        [:section#team
         [:h2 "Team"]
         (for [team-id data/team-ids
               :let [team-member (first (filter #(= (:id %) team-id) data/users))]]
           (comp/team-card team-member state))]
        [:section#about
         [:h2 "About"]
         (repeat 5 [:br])]
        [:section#reviews
         (comp/reviews-section state data/reviews)]
        ;; because address section has no content and is not tall enough, when clicking on it's navbar item, or
        ;; scroll all the way down, the Address nav-item does not get highlighted while Review does. No need to write
        ;; complex js code to prevent this since it won't be a problem once we add content to Reviews, Address and footer
        [:section#address
         [:h2 "Address"]
         (css/address-info
           icons/map-pin-line-icon
           [:p (:address m/shop-location)])
         (m/leaflet-map state)]))
    (css/footer
      [:h2 "La footer"])))
