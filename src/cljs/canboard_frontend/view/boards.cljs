(ns canboard-frontend.view.boards
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [canboard-frontend.api :as api]
            [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang]
            [canboard-frontend.util :as util]
            [canboard-frontend.route :as route]
            [canboard-frontend.view.parts :as parts]
            [canboard-frontend.view.lists :as lists]))

(defn toggle-board-creation
  [do-create]
  (swap! data/view-data assoc-in [:boards :creating] do-create))

(defn new-board-form
  "Form for a new board."
  []
  (letfn [(create-callback []
            (let [board-title-form (util/elem-by-id :board-title)
                  board-desc-form (util/elem-by-id :board-description)]
              (api/create-board! {:title (.-value board-title-form)
                                  :description (.-value board-desc-form)}
                                 (fn []
                                   (set! (.-value board-title-form) nil)
                                   (set! (.-value board-desc-form) nil)
                                   (.focus board-title-form)))))
          (key-callback [e] (when (= 13 (.-charCode e)) (create-callback)))
          (btn-callback [e] (create-callback))]
    [sa/Segment {:class "board-new-button piled card"}
     [:div.content
      [:p.header (lang/translate :boards :new)]
      [:div.ui.form
       [:div.field
        [:label (lang/translate :title)]
        [:input {:id :board-title :type :text :name :board-title :placeholder "Board Name"
                 :auto-focus true
                 :on-key-press key-callback}]]
       [:div.field
        [:label (lang/translate :description)]
        [:input {:id :board-description :type :text :name :board-description :placeholder "Board Description"
                 :on-key-press key-callback}]]]]
     [:div.extra.content.ui.two.buttons
      [sa/Button {:class "basic red"
                  :on-click #(toggle-board-creation false)}
       (lang/translate :do-cancel)]
      [sa/Button {:class "basic green"
                  :on-click btn-callback}
       (lang/translate :do-create)]]]))


(defn new-board-button
  "Component for a button that creates a new board.
  is-creating: Weather the button was clicked and should show form-data instead."
  [is-creating]
  (if (get-in @data/view-data [:boards :creating])
    [new-board-form]
    [sa/Segment {:class "board-new-button secondary piled card collapsed"}
     [:div.content {
                    :on-click #(toggle-board-creation true)}
      [sa/Icon {:class "plus"}]
      [:span {:class "middle aligned"}
       (lang/translate :boards :new)]]]))

(defn overview
  "Overview of the boards available to the current user."
  []
  (letfn [(new-board [] (api/create-board! {} identity))
          (delete-board [id] (when (js/confirm (lang/translate :confirm :deletion))
                               (api/delete-board! id identity)))]
    (r/create-class
     {:component-will-mount #(api/fetch-boards! identity)
      :display-name "boards-page"
      :reagent-render
      (fn []
        [:div#boards-overview-wrapper
         [:div {:class "ui cards"}
          (doall
           (for [[board-id board] @data/boards]
             [:div.overview-item-wrapper {:key board-id}
              [sa/Segment {:class "board-overview-item card"}
               [:div.content
                [:a.header {:href (route/board-route {:id board-id})}
                 (:title board)]
                [:span.description (:description board)]]
               [sa/Button {:class "extra content"
                           :on-click #(delete-board board-id)}
                (lang/translate :boards :delete)]]]))
          [:div.overview-item-wrapper
           [new-board-button]]]])})))

(defn board-page
  "View a single board."
  []
  (r/create-class
   {:component-will-mount #(api/fetch-board-lists! @data/current-board-id identity)
    :display-name "board-page"
    :reagent-render
    (fn []
      (let [cur-board-id @data/current-board-id]
        [:div#board-view-wrapper {:class "ui cards"}
         (for [[list-id list] @data/lists]
           [:div.list-item-wrapper {:key (str cur-board-id list-id)}
            [lists/single-list cur-board-id list-id list]])
         [:div.list-item-wrapper
          [lists/new-list-button cur-board-id]]]))}))
