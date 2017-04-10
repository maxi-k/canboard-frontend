(ns canboard-frontend.view.cards
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [canboard-frontend.api :as api]
            [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang]
            [canboard-frontend.util :as util]
            [canboard-frontend.route :as route]
            [canboard-frontend.view.parts :as parts]))

(defn new-card-field-id
  [list-id]
  (str "card-title-" list-id))

(defn toggle-card-creation
  [list-id do-create]
  (swap! data/view-data assoc-in [:cards :creating list-id] do-create))

(defn new-card-form
  "Form for creating a new card"
  [board-id list-id]
  (letfn [(create-callback []
            (let [card-form-elem (util/elem-by-id (new-card-field-id list-id))]
              (api/create-card! {:title (.-value card-form-elem)}
                                board-id
                                list-id
                                #(set! (.-value card-form-elem) nil))))
          (key-callback [e] (when (= 13 (.-charCode e)) (create-callback)))
          (btn-callback [e] (create-callback))]
    [sa/Segment {:class "card-new-button card"}
       [:div.content
        [:p.header (lang/translate :cards :new)]
        [:div.ui.form
         [:div.field
          [:label (lang/translate :title)]
          [:input {:id (new-card-field-id list-id) :type :text :name :card-title :placeholder "Card Name"
                   :auto-focus true
                   :on-key-press key-callback}]]]]
       [:div.extra.content.ui.two.buttons
        [sa/Button {:class "basic red"
                    :on-click #(toggle-card-creation list-id false)}
         (lang/translate :do-cancel)]
        [sa/Button {:class "basic green"
                    :on-click btn-callback}
         (lang/translate :do-create)]]]))

(defn new-card-button
  "Component for a button that creates a new card. "
  [board-id list-id]
  (if (get-in @data/view-data [:cards :creating list-id])
    [new-card-form board-id list-id]
    [sa/Segment {:class "card-new-button secondary card collapsed"}
     [:div.content {:on-click #(toggle-card-creation list-id true)}
      [sa/Icon {:class "plus"}]
      [:span {:class "middle aligned"}
       (lang/translate :cards :new)]]]))

(defn card-options
  "The dropdown menu that shows the options for a single card."
  [board-id list-id card-id]
  (letfn [(delete-card []
            (when true ;;(js/confirm (lang/translate :confirm :deletion))
              (api/delete-card! board-id list-id card-id identity)))]
    [sa/Dropdown {:icon "ellipsis horizontal"
                  :class "card-menu-button"}
     [sa/DropdownMenu
      [sa/DropdownItem {:on-click delete-card
                        :text (str " " (lang/translate :cards :delete))}]]]))

(defn single-card
  "A single card inside a list."
  [board-id list-id card-id card-data]
  [sa/Segment {:key card-id :class "card-overview-item"}
   [:span.card-title (card-data :title)]
   [card-options board-id list-id card-id]
   [:div.clearfloatlists]])
