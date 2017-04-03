(ns canboard-frontend.view.lists
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [canboard-frontend.api :as api]
            [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang]
            [canboard-frontend.util :as util]
            [canboard-frontend.route :as route]
            [canboard-frontend.view.parts :as parts]))

(defn toggle-list-creation
  [do-create]
  (swap! data/view-data assoc-in [:lists :creating] do-create))

(defn new-list-button
  "Component for a button that creates a new list. "
  [board-id]
  (letfn [(create-callback []
            (api/create-list! {:title (.-value (util/elem-by-id :list-title))}
                              board-id
                              (fn [] (toggle-list-creation false))))
          (key-callback [e] (when (= 13 (.-charCode e)) (create-callback)))
          (btn-callback [e] (create-callback))]
    (if (get-in @data/view-data [:lists :creating])
      [sa/Segment {:class "list-new-button piled card"}
       [:div.content
        [:p.header (lang/translate :lists :new)]
        [:div.ui.form
         [:div.field
          [:label (lang/translate :title)]
          [:input {:id :list-title :type :text :name :list-title :placeholder "List Name"
                   :on-key-press key-callback}]]]]
       [:div.extra.content.ui.two.buttons
        [sa/Button {:class "basic red"
                    :on-click #(toggle-list-creation false)}
         (lang/translate :do-cancel)]
        [sa/Button {:class "basic green"
                    :on-click btn-callback}
         (lang/translate :do-create)]]]
      [sa/Segment {:class "list-new-button secondary piled card collapsed"}
       [:div.content {:on-click #(toggle-list-creation true)}
        [sa/Icon {:class "plus"}]
        [:span {:class "middle aligned"}
         (lang/translate :lists :new)]]])))

(defn toggle-card-creation
  [list-id do-create]
  (swap! data/view-data assoc-in [:cards :creating list-id] do-create))

(defn new-card-button
  "Component for a button that creates a new card. "
  [board-id list-id]
  (letfn [(create-callback []
            (util/log "creating!"))
          (key-callback [e] (when (= 13 (.-charCode e)) (create-callback)))
          (btn-callback [e] (create-callback))]
    (if (get-in @data/view-data [:cards :creating list-id])
      [sa/Segment {:class "card-new-button card"}
       [:div.content
        [:p.header (lang/translate :cards :new)]
        [:div.ui.form
         [:div.field
          [:label (lang/translate :title)]
          [:input {:id :card-title :type :text :name :list-title :placeholder "Card Name"
                   :on-key-press key-callback}]]]]
       [:div.extra.content.ui.two.buttons
        [sa/Button {:class "basic red"
                    :on-click #(toggle-card-creation list-id false)}
         (lang/translate :do-cancel)]
        [sa/Button {:class "basic green"
                    :on-click btn-callback}
         (lang/translate :do-create)]]
       [:div.ui.bottom.attached.message "(Not implemented yet)"]]
      [sa/Segment {:class "card-new-button secondary card collapsed"}
       [:div.content {:on-click #(toggle-card-creation list-id true)}
        [sa/Icon {:class "plus"}]
        [:span {:class "middle aligned"}
         (lang/translate :cards :new)]]])))

(defn single-card
  "A single card inside a list."
  [board-id list-id card-id card-data]
  [:span (-> card-data :attributes :title)])

(defn single-list
  "A single list item to be rendered in the board view."
  [board-id list-id list-data]
  (r/create-class
   {:component-will-mount #(api/fetch-list-cards! board-id list-id identity)
    :display-name "single-list"
    :reagent-render
    (fn [board-id list-id list-data]
      (let [attr (-> list-data :attributes)]
        [sa/Segment {:class "list-item card segments"}
         [:div.content
          [:span.header (attr :title)]]
         (for [[card-id card] (attr :cards)]
           [sa/Segment {:key card-id :class "card-overview-item"}
            [single-card board-id list-id card-id card]])
         [new-card-button board-id list-id]]))}))
