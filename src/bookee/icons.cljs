(ns bookee.icons
  (:require [phosphor.icons :as icons]))

(def map-pin-line-icon
  (icons/render
    (icons/icon :phosphor.bold/map-pin-line)
    {:size 16}))

(def star-icon
  (icons/render
    (icons/icon :phosphor.fill/star)
    {:size 16}))

(def star-outline-icon
  (icons/render
    (icons/icon :phosphor.regular/star)
    {:size 16}))

(def chevron-down-icon
  (icons/render
    (icons/icon :phosphor.regular/caret-down)
    {:size 16}))

(def clock-icon
  (icons/render
    (icons/icon :phosphor.regular/clock)
    {:size 16}))

(def caret-left-icon
  (icons/render
    (icons/icon :phosphor.regular/caret-left)
    {:size 20}))

(def caret-right-icon
  (icons/render
    (icons/icon :phosphor.regular/caret-right)
    {:size 20}))
