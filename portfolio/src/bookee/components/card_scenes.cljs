(ns bookee.components.card-scenes
  (:require [bookee.components :as comp]
            [bookee.css :as css]
            [portfolio.replicant :refer [defscene]]
            [bookee.data :as data]))

(defscene service-card-collapsed
  (comp/service-card {:id 1
                      :service-name "Haircut & Style"
                      :duration 45
                      :price 35
                      :currency :euro
                      :details "Full service cut with consultation and styling"}
                     {}))

(defscene service-card-expanded
  (comp/service-card {:id 2
                      :service-name "Beard Trim"
                      :duration 20
                      :price 15
                      :currency :usd
                      :details "Professional beard shaping and grooming"}
                     {:ui {:details-visibility? {2 true}}}))

(defscene service-cards-mixed
  [:div {:style {:display "flex" :flex-direction "column" :gap "1rem"}}
   (comp/service-card {:id 3
                       :service-name "Quick Trim"
                       :duration 15
                       :price 10
                       :currency :euro}
                      {})
   (comp/service-card {:id 4
                       :service-name "Color Treatment"
                       :duration 90
                       :price 85
                       :currency :euro
                       :details "Full color service with consultation"}
                      {:ui {:details-visibility? {4 true}}})
   (comp/service-card {:id 5
                       :service-name "Hot Shave"
                       :duration 30
                       :price 25
                       :currency :usd
                       :details "Classic hot towel shave with premium products"}
                      {})])

(defscene team-card-collapsed
  [:div {:style {:width "400px"}}
   (comp/team-card {:id 20
                    :name "Alice"
                    :surname "Smith"
                    :img "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                    :details "Senior Stylist - 10 years experience"
                    :services-offered [1 2 3]}
                   {})])

(defscene team-card-expanded
  [:div {:style {:width "400px"}}
   (comp/team-card {:id 21
                    :name "Bob"
                    :surname "Johnson"
                    :img "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                    :details "Master Barber - Specializes in classic cuts"
                    :services-offered [1 2 5]}
                   {:ui {:details-visibility? {21 true}}})])

(defscene team-card-no-details
  [:div {:style {:width "400px"}}
   (comp/team-card {:id 22
                    :name "Charlie"
                    :surname "Brown"
                    :img "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                    :services-offered []}
                   {})])

(defscene team-cards-mixed-states
  [:div {:style {:display "flex" :flex-direction "column" :gap "1rem" :max-width "600px"}}
   (comp/team-card (assoc (first data/users) :details "Expert colorist and stylist")
                   {})
   (comp/team-card (assoc (nth data/users 1) :details "Beard specialist")
                   {:ui {:details-visibility? {(-> data/users (nth 1) :id) true}}})
   (comp/team-card (nth data/users 2)
                   {})])