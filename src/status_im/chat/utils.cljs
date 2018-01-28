(ns status-im.chat.utils
  (:require [status-im.chat.constants :as chat.constants]))

(defn add-message-to-db
  ([db chat-id message] (add-message-to-db db chat-id message true))
  ([db chat-id {:keys [message-id clock-value] :as message} new?]
   (let [prepared-message (assoc message
                                 :chat-id chat-id
                                 :new? (if (nil? new?) true new?))]
     (-> db
         (update-in [:chats chat-id :messages] assoc message-id prepared-message)
         (update-in [:chats chat-id :last-clock-value] (fnil max 0) clock-value)))))

(defn message-seen-by? [message user-pk]
  (= :seen (get-in message [:user-statuses user-pk])))

(defn command-name [{:keys [name]}]
  (str chat.constants/command-char name))
