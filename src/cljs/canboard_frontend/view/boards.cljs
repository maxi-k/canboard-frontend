(ns canboard-frontend.view.boards
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [canboard-frontend.api :as api]
            [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang]
            [canboard-frontend.util :as util]
            [canboard-frontend.route :as route]
            [canboard-frontend.view.parts :as parts]))

(defn toggle-board-creation
  [do-create]
  (swap! data/view-data assoc-in [:boards :creating] do-create))

(defn new-board-button
  "Component for a button that creates a new board.
  is-creating: Weather the button was clicked and should show form-data instead."
  [is-creating]
  (letfn [(create-callback []
            (api/create-board! {:title (.-value (util/elem-by-id :board-title))
                                :description (.-value (util/elem-by-id :board-description))}
                               (fn [] (route/goto! (route/boards-route))
                                 (toggle-board-creation false))))
          (key-callback [e] (when (= 13 (.-charCode e)) (create-callback)))
          (btn-callback [e] (create-callback))]
    (if (-> @data/view-data :boards :creating)
      [sa/Segment {:class "board-new-button piled card"}
       [:div.content
        [:p.header (lang/translate :boards :new)]
        [:div.ui.form
         [:div.field
          [:label (lang/translate :title)]
          [:input {:id :board-title :type :text :name :board-title :placeholder "Board Name"
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
         (lang/translate :do-create)]]]
      [sa/Segment {:class "board-new-button secondary piled card collapsed"}
       [:div.content {
                      :on-click #(toggle-board-creation true)}
        [sa/Icon {:class "plus"}]
        [:span {:class "middle aligned"}
         (lang/translate :boards :new)]]])))

(def overview
  "Overview of the boards available to the current user."
  (letfn [(new-board [] (api/create-board! {} identity))
          (delete-board [id] (api/delete-board! id identity))]
    (r/create-class
     {:component-will-mount #(api/fetch-boards! identity)
      :display-name "boards-page"
      :reagent-render
      (fn []
        (parts/default-template
         [:div#boards-overview-wrapper
          [:div {:class "ui cards"}
           (for [[board-id board] @data/boards
                 :let [attr (board :attributes)]]
             [:div.overview-item-wrapper {:key board-id}
              [sa/Segment {:class "board-overview-item card"}
               [:div.content
                [:a.header {:href (route/board-route {:id board-id})}
                 (:title attr)]
                [:span.description (:description attr)]]
               [sa/Button {:class "extra content"
                           :on-click #(delete-board board-id)}
                "Delete"]]])
           [:div.overview-item-wrapper
            [new-board-button]]]]))})))

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
    (if (-> @data/view-data :lists :creating)
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

(def board-page
  "View a single board."
  (r/create-class
   {:component-will-mount #(api/fetch-board-lists! @data/current-board-id identity)
    :display-name "board-page"
    :reagent-render
    (fn []
      (let [cur-board-id @data/current-board-id]
        (parts/default-template
         [:div#board-view-wrapper {:class "ui cards"}
          (for [[list-id list] @data/lists]
            [:div.list-item-wrapper {:key list-id}
             [sa/Segment {:class "list-item card"}
              [:div.content
               [:span.header (-> list :attributes :title)]]]])
          [:div.list-item-wrapper
           [new-list-button cur-board-id]]])))}))
