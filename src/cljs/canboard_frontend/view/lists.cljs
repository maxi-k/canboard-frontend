(ns canboard-frontend.view.lists
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [canboard-frontend.api :as api]
            [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang]
            [canboard-frontend.util :as util]
            [canboard-frontend.route :as route]
            [canboard-frontend.view.cards :as cards]
            [canboard-frontend.view.parts :as parts]))

(defn toggle-list-creation
  [do-create]
  (swap! data/view-data assoc-in [:lists :creating] do-create))

(defn new-list-form
  "Form for creating a new list."
  [board-id]
  (letfn [(create-callback []
            (let [list-form-elem (util/elem-by-id :list-title)]
             (api/create-list! {:title (.-value list-form-elem)}
                               board-id
                               #(set! (.-value list-form-elem) nil))))
          (key-callback [e] (when (= 13 (.-charCode e)) (create-callback)))
          (btn-callback [e] (create-callback))]
    (fn [board-id]
      [sa/Segment {:class "list-new-button piled card"}
       [:div.content
        [:p.header (lang/translate :lists :new)]
        [:div.ui.form
         [:div.field
          [:label (lang/translate :title)]
          [:input {:id :list-title :type :text :name :list-title :placeholder "List Name"
                   :auto-focus true
                   :on-key-press key-callback}]]]]
       [:div.extra.content.ui.two.buttons
        [sa/Button {:class "basic red"
                    :on-click #(toggle-list-creation false)}
         (lang/translate :do-cancel)]
        [sa/Button {:class "basic green"
                    :on-click btn-callback}
         (lang/translate :do-create)]]])))

(defn new-list-button
  "Component for a button that creates a new list. "
  [board-id]
  (if (get-in @data/view-data [:lists :creating])
    [new-list-form board-id]
    [sa/Segment {:class "list-new-button secondary piled card collapsed"}
     [:div.content {:on-click #(toggle-list-creation true)}
      [sa/Icon {:class "plus"}]
      [:span {:class "middle aligned"}
       (lang/translate :lists :new)]]]))

(defn list-options
  "The dropdown menu that shows the options for a list."
  [board-id list-id]
  (letfn [(delete-list []
            (when true ;; (js/confirm (lang/translate :confirm :deletion))
              (api/delete-list! board-id list-id identity)))]
    [sa/Dropdown {:icon "ellipsis horizontal"
                  :class "list-menu-button"}
     [sa/DropdownMenu
      [sa/DropdownItem {:on-click delete-list
                        :text (str " " (lang/translate :lists :delete))}]]]))

(defn single-list
  "A single list item to be rendered in the board view."
  [board-id list-id list-data]
  (r/create-class
   {:component-will-mount #(api/fetch-list-cards! board-id list-id identity)
    :display-name "single-list"
    :reagent-render
    (fn [board-id list-id list-data]
      [sa/Segment {:class "list-item card segments"}
       [:div.content
        [:span.header.list-title (list-data :title)]
        [list-options board-id list-id]
        [:div.clearfloat]]
       (for [[card-id card] (list-data :cards)]
         ^{:key card-id}
         [cards/card-overview-item board-id list-id card-id card])
       [cards/new-card-button board-id list-id]])}))
