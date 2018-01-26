(ns status-im.ui.components.list-selection
  (:require [re-frame.core :as re-frame]
            [status-im.ui.components.react :as react]
            [status-im.utils.platform :as platform]
            [status-im.i18n :as i18n]))

(defn open-share [content]
  (when (or (:message content)
            (:url content))
    (.share react/sharing (clj->js content))))

(defn share-options [text]
  [{:text  (i18n/label :t/sharing-copy-to-clipboard)
    :value #(react/copy-to-clipboard text)}
   {:text  (i18n/label :t/sharing-share)
    :value #(open-share {:message text})}])

(defn share [text dialog-title]
  (let [list-selection-fn (:list-selection-fn platform/platform-specific)]
    (list-selection-fn {:title       dialog-title
                        :options     (share-options text)
                        :callback    (fn [index]
                                       (case index
                                         0 (react/copy-to-clipboard text)
                                         1 (open-share {:message text})
                                         :default))
                        :cancel-text (i18n/label :t/sharing-cancel)})))

(defn browse [browse-command link]
  (let [list-selection-fn (:list-selection-fn platform/platform-specific)]
    (list-selection-fn {:title       (i18n/label :t/browsing-title)
                        :options     [{:text "@browse"}
                                      {:text (i18n/label :t/browsing-open-in-web-browser)}]
                        :callback    (fn [index]
                                       (case index
                                         0 (do
                                             (re-frame/dispatch [:select-chat-input-command
                                                                 (assoc browse-command :prefill [link])
                                                                 nil
                                                                 true])
                                             (js/setTimeout #(re-frame/dispatch [:send-current-message]) 100))
                                         1 (.openURL react/linking link)
                                         :default))
                        :cancel-text (i18n/label :t/browsing-cancel)})))

(defn share-or-open-map [address lat lng]
  (let [list-selection-fn (:list-selection-fn platform/platform-specific)]
    (list-selection-fn {:title       (i18n/label :t/location)
                        :options     [{:text  (i18n/label :t/sharing-copy-to-clipboard-address)}
                                      {:text  (i18n/label :t/sharing-copy-to-clipboard-coordinates)}
                                      {:text  (i18n/label :t/open-map)}]
                        :callback    (fn [index]
                                       (case index
                                         0 (react/copy-to-clipboard address)
                                         1 (react/copy-to-clipboard (str lng "," lat))
                                         2 (.openURL react/linking (if platform/ios?
                                                               (str "http://maps.apple.com/?ll=" lng "," lat)
                                                               (str "geo:" lng "," lat)))
                                         :default))
                        :cancel-text (i18n/label :t/cancel)})))
