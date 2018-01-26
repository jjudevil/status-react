(ns status-im.ui.components.icons.custom-icons
  (:require [reagent.core :as r]
            [status-im.react-native.js-dependencies :as js-dependencies]))

(def ion-icon
  (r/adapt-react-class (.-default js-dependencies/vector-icons)))
