(ns bookee.components
  (:require [bookee.css :as css]
            [bookee.data :as data]))


(def navbar
  (css/navbar
    [:ul
     [:li [:a {:href "#services"} "Services"]]
     [:li [:a {:href "#team"} "Team"]]
     [:li [:a {:href "#about"} "About"]]
     [:li [:a {:href "#reviews"} "Reviews"]]
     [:li [:a {:href "#address"} "Address"]]]))

(defn service-card
  [{:keys [id service-name duration price currency details] :as service} state]
  (let [details-visible? (get-in state [:ui :details-visibility? id] false)
        details-text     (if details-visible? "Hide details" "Details")]
    (css/service-card
      [:div.service
       [:h3 service-name]
       [:span (str duration " min") " mins • "]
       [:span (str (get data/currencies currency) price)]
       (when details
         [:span " • "
          [:a {:on {:click [[:toggle-details id]]}}
           (css/details-link details-text)]])
       (when (and details details-visible?)
         (css/details-body details))])))

(defn team-card
  [{:keys [id name surname img details services-offered] :as user} state]
  (let [details-visible? (get-in state [:ui :details-visibility? id] false)
        details-text     (if details-visible? "Hide details" "Details")]
    (css/team-card
      [:img.team-image {:src img :alt (str name " " surname)}]
      [:div.team-content
       [:div.team-name (str name " " surname)]
       (when details
         [:a {:on {:click [[:toggle-details id]]}}
          [:div.team-role (css/details-link details-text)]])
       (when (and details
                  details-visible?
                  (seq services-offered))
         (css/details-body
           [:div.team-role details]
           [:div.team-specialties
            (for [service-id services-offered
                  :let [service (first (filter #(= (:id %) service-id) data/services))]]
              [:span.specialty-badge {:key service-id}
               (:service-name service)])]))]
      [:div.team-arrow ">"])))