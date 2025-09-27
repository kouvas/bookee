(ns bookee.components
  (:require [bookee.css :as css]
            [bookee.data :as data]
            [bookee.icons :as icons]
            [clojure.string :as str]
            ["@js-joda/timezone" :as joda-tz]          ; load tz data
            [tick.core :as t]
            [tick.locale-en-us]))

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

(defn about
  [desc]
  (css/about-container
    [:p (:intro desc)]
    [:p (:tagline desc)]
    [:p [:strong (:closing desc)]]))

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

(defn get-current-status []
  ;; For now, use local time - timezone loading is complex in browser
  ;; TODO: Add proper timezone support with initialization
  (let [now          (t/now)
        current-time (t/time now)
        day-of-week  (t/day-of-week now)
        ;; Map Java day-of-week to our keyword format
        day-map      {:MONDAY    :monday
                      :TUESDAY   :tuesday
                      :WEDNESDAY :wednesday
                      :THURSDAY  :thursday
                      :FRIDAY    :friday
                      :SATURDAY  :saturday
                      :SUNDAY    :sunday}
        today-hours  (get data/working-hours (day-map day-of-week))
        open-time    (:open today-hours)
        close-time   (:close today-hours)]
    (cond
      ;; Shop is closed all day (no hours defined)
      (or (:closed? today-hours)
          (nil? open-time)
          (nil? close-time))
      {:status :closed :message "Closed"}

      ;; Before opening time
      (t/< current-time open-time)
      {:status :closed :message (str "Opens at " (t/format "h:mm a" open-time))}

      ;; After closing time
      (t/> current-time close-time)
      {:status :closed :message "Closed"}

      ;; Currently open
      :else
      {:status :open :message (str "Open • Closes at " (t/format "h:mm a" close-time))})))

(defn shop-banner
  [state reviews]
  (let [stats          (calculate-rating-stats reviews)
        hours-visible? (get-in state [:ui :details-visibility? :working-hours] false)
        current-status (get-current-status)]
    (css/shop-banner
      [:div.banner-content
       [:img.shop-logo
        {:src "img/barbershop.png"
         :alt "LaBarberShop logo"}]
       [:div.shop-info
        [:div.shop-header
         [:h1.shop-name (:name data/shop-info)]]
        [:div.rating-line
         [:span.rating-value (.toFixed (:average stats) 1)]
         [:span.star icons/star-icon]
         [:span.review-count (str "(" (:total stats) " reviews)")]]
        [:div.hours-container
         [:button.hours-button
          {:on {:click [[:ui/toggle-details :working-hours]]}}
          [:span icons/clock-icon]
          (if (= (:status current-status) :open)
            [:span {:class "open"} (:message current-status)]
            [:span {:class "closed"} (:message current-status)])
          [:span.chevron {:class (when hours-visible? "collapsed")}
           icons/chevron-down-icon]]]]]
      (when hours-visible?
        [:div.hours-dropdown
         [:div.hours-title "Business Hours"]
         [:div.hours-list
          (for [[day hours] (sort-by (fn [[k _]]
                                       (case k
                                         :monday 0 :tuesday 1 :wednesday 2
                                         :thursday 3 :friday 4 :saturday 5 :sunday 6))
                                     data/working-hours)]
            [:div.day-row {:key day}
             [:span (str/capitalize (name day))]
             [:span
              (if (or (:closed? hours) (nil? (:open hours)))
                [:span.closed "Closed"]
                (str (t/format "h:mm a" (:open hours)) " - " (t/format "h:mm a" (:close hours))))]])]]))))