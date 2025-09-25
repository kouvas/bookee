(ns bookee.components
  (:require [bookee.css :as css]
            [bookee.data :as data]
            [clojure.string :as str]))

(def navbar-links ["Services" "Team" "About" "Reviews" "Address"])
(defn navbar
  [state lifecycle-hooks]
  (css/navbar
    lifecycle-hooks
    [:ul
     (map (fn [link]
            (let [hashed-link (str "#" (str/lower-case link))]
              [:li {:key link}
               [:a {:class (when (= (->> state :ui :active-nav-link) hashed-link)
                             "active")
                    :href  hashed-link
                    :on    {:click [[:set-active-nav-link hashed-link]]}}
                link]]))
          navbar-links)]))

(defn service-card
  [{:keys [id service-name duration price currency details] :as service} state]
  (let [details-visible? (get-in state [:ui :details-visibility? id] false)
        details-text     (if details-visible? "Hide details" "Details")]
    (css/service-card
      {:id (str "service_" id)}
      [:h3 service-name]
      [:span (str duration " min") " mins • "]
      [:span (str (get data/currencies currency) price)]
      (when details
        [:span " • "
         [:a {:on {:click [[:toggle-details id]]}}
          (css/details-link details-text)]])
      (when (and details details-visible?)
        (css/details-body details)))))

(defn team-card
  [{:keys [id name surname img details services-offered] :as user} state]
  (let [details-visible? (get-in state [:ui :details-visibility? id] false)
        details-text     (if details-visible? "Hide details" "Details")]
    (css/team-card
      {:id (str "team_member_" id)}
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