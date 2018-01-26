(ns status-im.ui.components.mapbox
  (:require [reagent.core :as reagent]
            [status-im.react-native.js-dependencies :as js-dependencies]))

(defn get-property [name]
  (aget js-dependencies/mapbox-gl name))

(defn adapt-class [class]
  (when class
    (reagent/adapt-react-class class)))

(defn get-class [name]
  (adapt-class (get-property name)))

(.setAccessToken js-dependencies/mapbox-gl "pk.eyJ1Ijoic3RhdHVzaW0iLCJhIjoiY2oydmtnZjRrMDA3czMzcW9kemR4N2lxayJ9.Rz8L6xdHBjfO8cR3CDf3Cw")

(def mapview (get-class "MapView"))
