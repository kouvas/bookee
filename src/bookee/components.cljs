(ns bookee.components
  (:require [bookee.css :as css]
            [bookee.data :as data]
            [bookee.icons :as icons]
            [clojure.string :as str]
            ["@js-joda/timezone" :as joda-tz]          ; load tz data
            [taoensso.telemere :as tel]
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
        details-text     (if details-visible? "Hide details" "Details")
        selected?        (= (get-in state [:booking-details :selected-service]) id)
        current-view     (get-in state [:navigation :current-view])]
    (css/service-card
      {:id    (str "service_" id)
       :class (when selected? "selected")
       :on    (when (#{:main :services} current-view)
                {:click [[:select-service id]]})}
      [:h3 service-name]
      [:div.service-info
       [:div
        [:span (str duration " min") " mins • "]
        [:span (str (get data/currencies currency) price)]
        (when details
          [:span " • "
           (css/details-link
             {:on {:click [[:ui/stop-propagation] [:ui/toggle-details id]]}}
             details-text)])]
       [:span.arrow ">"]]
      (when (and details details-visible?)
        (css/details-body details)))))

(defn team-card
  [{:keys [id name surname img details services-offered] :as user} state]
  (let [details-visible? (get-in state [:ui :details-visibility? id] false)
        details-text     (if details-visible? "Hide details" "Details")
        selected?        (= (get-in state [:booking-details :selected-team-member]) id)
        current-view     (get-in state [:navigation :current-view])]
    (css/team-card
      {:id    (str "team_member_" id)
       :class (when selected? "selected")
       :on    (when (#{:main :team} current-view)
                {:click [[:select-team id]]})}
      [:img.team-image {:src img :alt (str name " " surname)}]
      [:div.team-content
       [:div.team-name (str name " " surname)]
       (when details
         [:div.team-role
          (css/details-link
            {:on {:click [[:ui/stop-propagation] [:ui/toggle-details id]]}}
            details-text)])
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
      [:span.arrow ">"])))

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
        day-map      {"MONDAY"    :monday
                      "TUESDAY"   :tuesday
                      "WEDNESDAY" :wednesday
                      "THURSDAY"  :thursday
                      "FRIDAY"    :friday
                      "SATURDAY"  :saturday
                      "SUNDAY"    :sunday}
        today-hours  (get data/working-hours (get day-map (t/format day-of-week)))
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

(defn base-shop-banner
  [state reviews]
  (let [stats          (calculate-rating-stats reviews)
        hours-visible? (get-in state [:ui :details-visibility? :working-hours] false)
        current-status (get-current-status)]
    [:div
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
         (let [opts {:on {:click [[:ui/toggle-details :working-hours]]}}]
           (if (= (:status current-status) :open)
             (assoc opts :class "open")
             (assoc opts :class "closed")))
         [:span icons/clock-icon]
         [:span (:message current-status)]
         [:span.chevron {:class (when hours-visible? "collapsed")}
          icons/chevron-down-icon]]]]]
     [:div
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
                (str (t/format "h:mm a" (:open hours)) " - " (t/format "h:mm a" (:close hours))))]])]])]]))


(defn top-shop-banner
  [state reviews]
  (css/top-shop-banner
    (base-shop-banner state reviews)))

(defn side-shop-banner
  [state reviews]
  (css/side-shop-banner
    (base-shop-banner state reviews)))

(defn booking-summary [state]
  (let [service-id      (get-in state [:booking-details :selected-service])
        team-id         (get-in state [:booking-details :selected-team-member])
        date-str        (get-in state [:booking-details :selected-date])
        time-str        (get-in state [:booking-details :selected-time])
        service         (first (filter #(= (:id %) service-id) data/services))
        team            (first (filter #(= (:id %) team-id) data/users))
        date            (when date-str (t/date date-str))
        day-name        (when date (str/capitalize (str (t/day-of-week date))))
        day-num         (when date (t/day-of-month date))
        month-name      (when date (str/capitalize (t/format (t/month date))))
        formatted-date  (when date (str day-name ", " month-name " " day-num))
        full-name       (when team (str (:name team) " " (:surname team)))
        currency-symbol (get data/currencies (:currency service))]
    (css/booking-summary
      [:div.summary-title "Booking Summary"]
      [:div.summary-item
       [:span.item-label "Service"]
       [:span.item-value (:service-name service)]]
      [:div.summary-item
       [:span.item-label "Barber"]
       [:span.item-value full-name]]
      [:div.summary-item
       [:span.item-label "Date"]
       [:span.item-value formatted-date]]
      [:div.summary-item
       [:span.item-label "Time"]
       [:span.item-value (or time-str "Not selected")]]
      [:div.summary-item
       [:span.item-label "Price"]
       [:span.price-value (str currency-symbol (:price service))]])))


(defn confirmation-modal [state]
  (when (get-in state [:ui :show-confirmation-modal?])
    (css/modal-overlay
      {:on {:click [[:verification/close-modal]]}}
      [:div.modal-content
       {:on {:click [[:ui/stop-propagation]]}}
       [:div.modal-title "Magnifique! 💈"]
       [:div.modal-message
        "Your booking is confirmed! We'll see you soon for a haircut so good, "
        "even the Mona Lisa would smile with envy!"]
       [:button.modal-button
        {:on {:click [[:verification/close-modal]]}}
        "Merci!"]])))


