(ns bookee.ui
  (:require [bookee.css :as css]
            [bookee.components :as comp]
            [bookee.data :as data]))

(def shop-name "La BarberShop")
(def navbar
  (css/navbar
    [:ul
     [:li [:a {:href "#services"} "Services"]]
     [:li [:a {:href "#team"} "Team"]]
     [:li [:a {:href "#about"} "About"]]
     [:li [:a {:href "#reviews"} "Reviews"]]
     [:li [:a {:href "#address"} "Address"]]]))

(defn main
  [state]
  (css/content
    navbar
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
           (comp/team-card team-member))]
        [:section#about
         [:h2 "About"]
         (repeat 5 [:br])]
        [:section#reviews
         [:h2 "Reviews"]
         [:p "Be the first to review us and share insights about your experience."]
         (repeat 5 [:br])]
        [:section#Address
         [:h2 "Address"]
         (repeat 5 [:br])]))
    (css/footer
      [:h2 "La footer"])))
