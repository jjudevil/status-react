(ns status-im.ui.components.toolbar.view
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.sync-state.gradient :as sync-state.gradient]
            [status-im.ui.components.styles :as components.styles]
            [status-im.ui.components.context-menu :as context-menu]
            [status-im.ui.components.toolbar.actions :as actions]
            [status-im.ui.components.toolbar.styles :as styles]
            [status-im.ui.components.icons.vector-icons :as vector-icons]
            [status-im.utils.platform :as platform]))

;; Navigation item

(defn nav-item
  [{:keys [handler accessibility-label style] :or {handler #(re-frame/dispatch [:navigate-back])}} item]
  [react/touchable-highlight
   (merge {:on-press handler}
          (when accessibility-label
            {:accessibility-label accessibility-label}))
   [react/view {:style style}
    item]])

(defn nav-button
  [{:keys [icon icon-opts] :as props}]
  [nav-item (merge {:style styles/nav-item-button} props)
   [vector-icons/icon icon icon-opts]])

(defn nav-text
  ([text] (nav-text text nil))
  ([text handler] (nav-text nil text handler))
  ([props text handler]
   [react/text (merge {:style (merge styles/item styles/item-text) :on-press (or handler #(re-frame/dispatch [:navigate-back]))})
    text]))

(defn nav-clear-text
  ([text] (nav-clear-text text nil))
  ([text handler]
   (nav-text styles/item-text-white-background text handler)))

(def default-nav-back [nav-button actions/default-back])

;; Content

(defn content-wrapper [content]
  [react/view {:style styles/toolbar-container}
   content])

(defn content-title
  ([title] (content-title nil title))
  ([title-style title]
   (content-title title-style title nil nil))
  ([title-style title subtitle-style subtitle]
   [react/view {:style styles/toolbar-title-container}
    [react/text {:style (merge styles/toolbar-title-text title-style)
              :font     :toolbar-title}
     title]
    (when subtitle [react/text {:style subtitle-style} subtitle])]))

;; Actions

(defn text-action [{:keys [style handler disabled?]} title]
  [react/text {:style (merge styles/item styles/item-text style
                             (when disabled? styles/toolbar-text-action-disabled))
            :on-press (when-not disabled? handler)}
   title])

(def blank-action [react/view {:style (merge styles/item styles/toolbar-action)}])

(defn- option-actions [icon icon-opts options]
  [context-menu/context-menu
   [react/view {:style styles/toolbar-action}
    [vector-icons/icon icon icon-opts]]
   options
   nil
   styles/item])

(defn- icon-action [icon {:keys [overlay-style] :as icon-opts} handler]
  [react/touchable-highlight {:on-press handler}
   [react/view {:style (merge styles/item styles/toolbar-action)}
    (when overlay-style
      [react/view overlay-style])
    [vector-icons/icon icon icon-opts]]])

(defn actions [v]
  [react/view {:style styles/toolbar-actions}
   (for [{:keys [image icon icon-opts options handler]} v]
     (with-meta
       (cond (= image :blank)
             blank-action

             options
             [option-actions icon icon-opts options]

             :else
             [icon-action icon icon-opts handler])
       {:key (str "action-" (or image icon))}))])

(defn toolbar
  ([props nav-item content-item] (toolbar props nav-item content-item [actions [{:image :blank}]]))
  ([{:keys [background-color style flat? show-sync-bar?]}
    nav-item
    content-item
    action-items]
   ;; TODO remove extra view wen we remove sync-state-gradient
   [react/view
    [react/view {:style (merge (styles/toolbar background-color flat?) style)}
     ;; On iOS title must be centered. Current solution is a workaround and eventually this will be sorted out using flex
     (when platform/ios?
       [react/view styles/ios-content-item
        content-item])
     (when nav-item
       [react/view {:style (styles/toolbar-nav-actions-container 0)}
        nav-item])
     (if platform/ios?
       [react/view components.styles/flex]
       content-item)
     action-items]
    (when show-sync-bar? [sync-state.gradient/sync-state-gradient-view])]))

(defn simple-toolbar
  "A simple toolbar whose content is a single line text"
  ([] (simple-toolbar nil))
  ([title] (toolbar nil default-nav-back [content-title title])))

(def search-text-input (reagent/atom nil))

(defn- toolbar-search-submit [on-search-submit]
  (let [text @(re-frame/subscribe [:get-in [:toolbar-search :text]])]
    (on-search-submit text)
    (re-frame/dispatch [:set-in [:toolbar-search :text] nil])))

(defn- toolbar-with-search-content [{:keys [show-search?
                                            search-placeholder
                                            title
                                            custom-title
                                            on-search-submit]}]
  [react/view styles/toolbar-with-search-content
   (if show-search?
     [react/text-input
      {:style                  styles/toolbar-search-input
       :ref                    #(reset! search-text-input %)
       :auto-focus             true
       :placeholder            search-placeholder
       :placeholder-text-color components.styles/color-gray4
       :on-change-text         #(re-frame/dispatch [:set-in [:toolbar-search :text] %])
       :on-submit-editing      (when on-search-submit
                                 #(toolbar-search-submit on-search-submit))}]
     (or custom-title
         [react/view
          [react/text {:style styles/toolbar-title-text
                    :font     :toolbar-title}
           title]]))])

(defn- toggle-search-fn [text]
  (re-frame/dispatch [:set-in [:toolbar-search :show] text])
  (re-frame/dispatch [:set-in [:toolbar-search :text] ""]))

(defn- search-actions [show-search? search-text search-key actions]
  (if show-search?
    (if (pos? (count search-text))
      [(actions/close #(do
                     (.clear @search-text-input)
                     (re-frame/dispatch [:set-in [:toolbar-search :text] ""])))]
      [actions/search-icon])
    (into [(actions/search #(toggle-search-fn search-key))] actions)))


(defn toolbar-with-search [{:keys [show-search?
                                   search-text
                                   search-key
                                   nav-action
                                   style
                                   modal?]
                            :as   opts}]
  ;; TODO(jeluard) refactor to components? Drop modal? and nav-action support
  [toolbar {:modal? modal?
             :style style}
   [nav-button
    (if show-search?
      (actions/back #(toggle-search-fn nil))
      (or nav-action (if modal? actions/default-close actions/default-back)))]
   [toolbar-with-search-content opts]
   [actions (search-actions show-search? search-text search-key (:actions opts))]])