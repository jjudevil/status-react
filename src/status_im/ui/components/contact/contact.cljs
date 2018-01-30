(ns status-im.ui.components.contact.contact
  (:require-macros [status-im.utils.views :refer [defview letsubs]])
  (:require [status-im.ui.components.react :as react]
            [status-im.ui.components.icons.vector-icons :as vector-icons]
            [status-im.ui.components.chat-icon.screen :as chat-icon.screen]
            [status-im.ui.components.context-menu :as context-menu]
            [status-im.ui.components.contact.styles :as styles]
            [status-im.utils.gfycat.core :as gfycat]
            [status-im.i18n :as i18n]))

(defn contact-photo [contact]
  [react/view
   [chat-icon.screen/contact-icon-contacts-tab contact]])

(defn contact-inner-view
  ([{:keys [info style] {:keys [whisper-identity name] :as contact} :contact}]
   [react/view (merge styles/contact-inner-container style)
    [contact-photo contact]
    [react/view styles/info-container
     [react/text {:style     styles/name-text
            :number-of-lines 1}
      (if (pos? (count (:name contact)))
        (i18n/get-contact-translated whisper-identity :name name)
        ;;TODO is this correct behaviour?
        (gfycat/generate-gfy whisper-identity))]
     (when info
       [react/text {:style styles/info-text}
        info])]]))

(defn contact-view [{:keys [contact extended? on-press extend-options info]}]
  [react/touchable-highlight (when-not extended?
                         {:on-press (when on-press #(on-press contact))})
   [react/view
    [react/view styles/contact-container
     [contact-inner-view {:contact contact :info info}]
     (when (and extended? (not (empty? extend-options)))
       [react/view styles/more-btn-container
        [context-menu/context-menu
         [vector-icons/icon :icons/options {:accessibility-label :options}]
         extend-options
         nil
         styles/more-btn]])]]])

(defview toogle-contact-view [{:keys [whisper-identity] :as contact} selected-key on-toggle-handler]
  (letsubs [checked [selected-key whisper-identity]]
    [react/touchable-highlight {:on-press #(on-toggle-handler checked whisper-identity)}
     [react/view
      [react/view (merge styles/contact-container (when checked {:style styles/selected-contact}))
       [contact-inner-view (merge {:contact contact}
                                  (when checked {:style styles/selected-contact}))]
       [react/view (styles/icon-check-container checked)
        (when checked
          [vector-icons/icon :icons/ok {:style styles/check-icon}])]]]]))
