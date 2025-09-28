(ns bookee.navigation
  (:require [com.fulcrologic.statecharts :as sc]
            [com.fulcrologic.statecharts.chart :refer [statechart]]
            [com.fulcrologic.statecharts.elements :refer [state transition]]
            [com.fulcrologic.statecharts.events :refer [new-event]]
            [com.fulcrologic.statecharts.protocols :as sp]
            [com.fulcrologic.statecharts.simple :as simple]))

(def booking-statechart
  (statechart {::sc/initial :main}
              (state {:id :main}
                     (transition {:event :select-service :target :->team})
                     (transition {:event :select-team :target :->service}))

              (state {:id :->team}
                     (transition {:event :select-team :target :service->team->calendar})
                     (transition {:event :go-back :target :main}))

              (state {:id :->service}
                     (transition {:event :select-service :target :team->service->calendar})
                     (transition {:event :go-back :target :main}))

              (state {:id :service->team->calendar}
                     (transition {:event :go-back :target :->team})
                     (transition {:event :go-forward :target :service->team->calendar->verification}))

              (state {:id :team->service->calendar}
                     (transition {:event :go-back :target :->service})
                     (transition {:event :go-forward :target :team->service->calendar->verification}))

              (state {:id :service->team->calendar->verification}
                     (transition {:event :go-back :target :service->team->calendar}))

              (state {:id :team->service->calendar->verification}
                     (transition {:event :go-back :target :team->service->calendar}))))

(def state->view
  {:main                                  :main
   :->team                                :team
   :->service                             :services
   :service->team->calendar               :calendar
   :team->service->calendar               :calendar
   :service->team->calendar->verification :verification
   :team->service->calendar->verification :verification
   })

(defonce env (simple/simple-env))
(simple/register! env :booking booking-statechart)

(defn get-view [state-key]
  (get state->view state-key :main))

(defn can-go-back? [state-key]
  (not= state-key :main))

(defn can-go-forward? [state-key selected-service selected-team]
  (and (#{:service->team->calendar :team->service->calendar} state-key)
       selected-service
       selected-team))

(defn get-current-state [wmem]
  (first (::sc/configuration wmem)))

(defn init-statechart []
  (let [processor (::sc/processor env)]
    (sp/start! processor env :booking {::sc/session-id 1})))

(defn process-event [wmem event-type]
  (let [processor (::sc/processor env)]
    (sp/process-event! processor env wmem (new-event event-type))))

(comment
  (require '[com.fulcrologic.statecharts.algorithms.v20150901-validation :as v])
  (v/problems booking-statechart))
