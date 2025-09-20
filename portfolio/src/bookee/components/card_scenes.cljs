(ns bookee.components.card-scenes
  (:require [bookee.components :as comp]
            [bookee.css :as css]
            [portfolio.replicant :refer [defscene]]
            [bookee.data :as data]))

(defscene base-card
  ;; Direct use of CSS class since base-card is just styling
  (css/base-card
    [:h2 "Base Content Card"]
    [:p "This card contains content."]))

(defscene service-card
  (comp/service-card {:service-name "BuzCut"
                      :duration     30
                      :price        12
                      :currency     :euro
                      :details      "Da best cut"}
                     {}))

(defscene team-card
  [:div {:style {:width "400px"}}
   (comp/team-card (first (filter #(= (:id %) 10) data/users)))])

(defscene team-cards-list
  [:div {:style {:max-width "600px"}}
   (for [team-id data/team-ids
         :let [team-member (first (filter #(= (:id %) team-id) data/users))]]
     (comp/team-card team-member))])

(defscene team-card-hover
  [:div {:style {:width "400px"}}
   [:div {:style {:transform  "translateY(-2px)"
                  :box-shadow "0 4px 16px rgba(0,0,0,0.15)"
                  :transition "all 0.3s ease"}}
    (comp/team-card (nth data/users 1))]])

(defscene team-card-minimal
  [:div {:style {:width "400px"}}
   (comp/team-card {:id               99
                    :name             "John"
                    :surname          "Doe"
                    :img              "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                    :details          "New team member"
                    :services-offered []})])

(defscene card-inheritance-demo
  [:div {:style {:max-width "800px"}}
   [:h3 {:style {:margin-bottom "1rem"}} "All cards inherit from base-card:"]

   [:div {:style {:margin-bottom "0.5rem"}}
    (css/base-card
      [:h4 "Base Card"]
      [:p "This is a plain base-card with default styling."])]

   [:div {:style {:margin-bottom "0.5rem"}}
    (comp/service-card {:service-name "Haircut"
                        :duration     30
                        :price        40
                        :currency     :usd
                        :details      "Professional cut"}
                       {})]

   [:div {:style {:margin-bottom "0.5rem"}}
    (comp/team-card (first data/users))]

   [:p {:style {:margin-top       "1rem"
                :padding          "1rem"
                :background-color "#f3f4f6"
                :border-radius    "0.5rem"}}
    "All cards share: same background, border-radius, shadow, hover effects, transitions. "
    "Each card type onlydds its specific styling!"]])