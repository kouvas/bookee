(ns bookee.map
  (:require [bookee.css :as css]))

(def shop-location
  {:lat     48.8583596
   :lng     2.2949375
   :zoom    15
   :address "Quai Jacques Chirac, 75007 Paris, France"})

(defn load-leaflet-css! []
  (when-not (.getElementById js/document "leaflet-css")
    (let [link (.createElement js/document "link")]
      (set! (.-id link) "leaflet-css")
      (set! (.-rel link) "stylesheet")
      (set! (.-href link) "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css")
      (set! (.-integrity link) "sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=")
      (set! (.-crossOrigin link) "")
      (.appendChild (.-head js/document) link))))

(defn load-leaflet-js! [callback]
  (if (exists? js/L)
    (callback)
    (let [script (.createElement js/document "script")]
      (set! (.-src script) "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js")
      (set! (.-integrity script) "sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=")
      (set! (.-crossOrigin script) "")
      (set! (.-onload script) callback)
      (.appendChild (.-body js/document) script))))

(defn leaflet-map
  [_state]
  (css/map-container
    {:id                 "map"
     :replicant/on-mount [[:map/init-leaflet]]}))

(defn init-leaflet!
  [store]
  (load-leaflet-css!)
  (load-leaflet-js!
    (fn []
      (when-let [container (.getElementById js/document "map")]
        (when-not (.-_leaflet_id container)
          (let [{:keys [lat lng zoom]} shop-location
                map-instance (.map js/L container #js {:zoomControl true})
                tile-layer   "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution  "&copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors"]

            (.setView map-instance #js [lat lng] zoom)

            (.addTo (.tileLayer js/L tile-layer
                                #js {:attribution attribution
                                     :maxZoom     19})
                    map-instance)

            (.addTo (.marker js/L #js [lat lng])
                    map-instance)

            (swap! store assoc-in [:ui :map-instance] map-instance)))))))
