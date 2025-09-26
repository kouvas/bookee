(ns bookee.components
  (:require [bookee.css :as css]
            [bookee.data :as data]
            [bookee.icons :as icons]
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
                    :on    {:click [[:ui/set-active-nav-link hashed-link]]}}
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
         [:a {:on {:click [[:ui/toggle-details id]]}}
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
         [:a {:on {:click [[:ui/toggle-details id]]}}
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

(defn star-rating [rating]
  [:div.stars
   (for [i (range 1 6)]
     (if (<= i rating)
       [:span.star {:key i} icons/star-icon]
       [:span.star-empty {:key i} icons/star-outline-icon]))])

(defn review-item [{:keys [author rating review date]}]
  (css/review-item
    [:div.review-header
     [:div.review-author
      [:span.author-name author]
      [:div.review-rating (star-rating rating)]
      [:span.review-date date]]]
    [:div.review-text review]))

(defn calculate-rating-stats [reviews]
  (let [total         (count reviews)
        avg-rating    (if (pos? total)
                        (/ (reduce + (map :rating reviews)) total)
                        0)
        rating-counts (frequencies (map :rating reviews))]
    {:total        total
     :average      avg-rating
     :distribution (for [i (range 5 0 -1)]
                     {:rating     i
                      :count      (get rating-counts i 0)
                      :percentage (if (pos? total)
                                    (* 100 (/ (get rating-counts i 0) total))
                                    0)})}))

(defn reviews-section [state reviews]
  (let [stats (calculate-rating-stats reviews)]
    (css/reviews-container
      [:h2 "Reviews"]
      (css/rating-summary
        [:div.rating-display
         [:span.rating-number (.toFixed (:average stats) 1)]
         (star-rating (Math/round (:average stats)))]
        [:span.rating-count (str (:total stats) " reviews")]
        [:div.rating-bars
         (for [{:keys [rating count percentage]} (:distribution stats)]
           [:div.rating-row {:key rating}
            [:div.rating-label
             [:span (str rating)]
             [:span.star icons/star-icon]]
            [:div.rating-bar
             [:div.rating-fill {:style {:width (str percentage "%")}}]]
            [:div.rating-value count]])])
      (for [review reviews]
        [:div {:key (:id review)}
         (review-item review)]))))