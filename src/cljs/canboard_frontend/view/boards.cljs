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
                               (fn [] (route/goto! (route/boards-route)))))
          (key-callback [e] (when (= 13 (.-charCode e)) (create-callback)))
          (btn-callback [e] (create-callback))]
    (if (-> @data/view-data :boards :creating)
      [sa/Segment {:class "board-new-button green card"}
       [:div.content
        [:div.ui.form
         [:div.field
          [:label (lang/translate :title)]
          [:input {:id :board-title :type :text :name :board-title :placeholder "Board Name"
                   :on-key-press key-callback}]]
         [:div.field
          [:label (lang/translate :description)]
          [:input {:id :board-description :type :text :name :board-description :placeholder "Board Description"
                   :on-key-press key-callback}]]
         ]]
       [:div.extra.content
        [sa/Button {:on-click #(toggle-board-creation false)}
         (lang/translate :do-cancel)]
        [sa/Button {:on-click btn-callback}
         (lang/translate :do-create)]]]
      [sa/Segment {:class "board-new-button green card"
                   :href "#"
                   :on-click #(toggle-board-creation true)}
       [:div.content
        [sa/Icon {:class "plus"}]
        [:span (lang/translate :boards :new)]]])))

(def overview
  "Overview of the boards available to the current user."
  (letfn [(new-board []
            (api/create-board! {} identity))]
    (r/create-class
     {:component-will-mount #(api/fetch-boards! identity)
      :display-name "boards-page"
      :reagent-render
      (fn []
        (parts/default-template
         [:div#boards-overview-wrapper
          [:div {:class "ui cards"}
           (for [board @data/boards
                 :let [attr (get board :attributes)]]
             [sa/Segment {:key (:id board)
                          :href (route/board-route {:id (:id board)})
                          :class "board-overview-item card"}
              [:div.content
               (:title attr)]])]
          [new-board-button]]))})))


(def board-page
  "View a single board."
  (r/create-class
   {:component-will-mount #(api/fetch-board-data! (@data/current-board :id) identity)
    :display-name "board-page"
    :reagent-render
    (fn []
      (let [cur-board @data/current-board]
        (parts/default-template
         [:div#board-view-wrapper {:class "ui cards"}
          (for [list @data/lists]
            [sa/Segment {:key (:id list)
                         :class "list-item card"
                         :href (route/list-route {:board_id (:id cur-board)
                                                  :id (:id list)})}
             [:div.content [:span (:title list)]]])])))}))
