(ns status-im.ui.components.context-menu
  (:require [status-im.ui.components.styles :as components.styles]
            [status-im.utils.platform :as platform]
            [status-im.ui.components.react :as react]
            [status-im.react-native.js-dependencies :as js-dependencies]))

(defn- get-property [name]
  (aget js-dependencies/popup-menu name))

(defn- get-class [name]
  (react/adapt-class (get-property name)))

(def menu (get-class "Menu"))
(def menu-context (get-class "MenuContext"))
(def menu-trigger (get-class "MenuTrigger"))
(def menu-options (get-class "MenuOptions"))
(def menu-option (get-class "MenuOption"))

(defn context-menu-options [custom-styles]
  {:customStyles {:optionsContainer
                  (merge {:elevation      2
                          :margin-top     0
                          :padding-top    8
                          :width          164
                          :padding-bottom 8}
                         (:optionsContainer custom-styles))
                  :optionWrapper
                  (merge {:padding-left    16
                          :padding-right   16
                          :justify-content :center
                          :height          48}
                         (:optionWrapper custom-styles))}})

(defn context-menu-text [destructive?]
  {:font-size   15
   :line-height 20
   :color       (if destructive? components.styles/color-light-red components.styles/text1-color)})

(def list-selection-fn (:list-selection-fn platform/platform-specific))

(defn open-ios-menu [title options]
  (list-selection-fn {:options  options
                      :title    title
                      :callback (fn [index]
                                  (when (< index (count options))
                                    (when-let [handler (:value (nth options index))]
                                      (handler))))})
  nil)

(defn context-menu [trigger options & custom-styles trigger-style]
  (if platform/ios?
    [react/touchable-highlight {:style trigger-style
                             :on-press #(open-ios-menu nil options)}
     [react/view
      trigger]]
    [menu {:onSelect #(when % (do (%) nil))}
     [menu-trigger {:style trigger-style} trigger]
     [menu-options (context-menu-options custom-styles)
      (for [{:keys [style value destructive?] :as option} options]
        ^{:key option}
        [menu-option {:value value}
         [react/text {:style (merge (context-menu-text destructive?) style)}
          (:text option)]])]]))

(defn modal-menu [trigger style title options]
  [react/touchable-highlight {:style style
                           :on-press #(open-ios-menu title options)}
   [react/view
    trigger]])
