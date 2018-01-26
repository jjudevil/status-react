(ns status-im.ui.components.sticky-button
  (:require-macros [status-im.utils.styles :refer [defstyle defnstyle]])
  (:require [status-im.ui.components.styles :as components.styles]
            [status-im.utils.platform :as platform]
            [status-im.utils.utils :as utils]
            [status-im.ui.components.react :as react]))

(def sticky-button-style
  {:flex-direction   :row
   :height           52
   :justify-content  :center
   :align-items      :center
   :background-color components.styles/color-light-blue})

(defstyle sticky-button-label-style
  {:color   components.styles/color-white
   :ios     {:font-size      17
             :line-height    20
             :letter-spacing -0.2}
   :android {:font-size      14
             :letter-spacing 0.5}})

(defn sticky-button
  ([label on-press] (sticky-button label on-press false))
  ([label on-press once?]
   [react/touchable-highlight {:on-press (if once? (utils/wrap-call-once! on-press) on-press)}
    [react/view sticky-button-style
     [react/text {:style sticky-button-label-style
                  :uppercase? (get-in platform/platform-specific [:uppercase?])}
           label]]]))