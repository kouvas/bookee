(ns bookee.icons
  (:require [phosphor.icons :as icons]))

(def map-pin-line-icon
  (icons/render
    (icons/icon :phosphor.bold/map-pin-line)
    {:size 20}))

(def star-icon
  (icons/render
    (icons/icon :phosphor.fill/star)
    {:size 16}))

(def star-outline-icon
  (icons/render
    (icons/icon :phosphor.regular/star)
    {:size 16}))
