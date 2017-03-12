(ns canboard-frontend.view.boards
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [canboard-frontend.api :as api]
            [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang]
            [canboard-frontend.route :as route]
            [canboard-frontend.view.parts :as parts]))

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
         [:div#boards-overview-wrapper {:class "ui cards"}
          (for [board @data/boards
                :let [attr (board :attributes)]]
            [sa/Segment {:key (:id board)
                         :class "board-overview-item card"}
             [:div.content
              [:a {:href (route/board-route {:id (:id board)})}
               (:title attr)]]])
          [sa/Segment {:class "board-new-button card green"
                       :onclick new-board}
           [:div.content
            [sa/Icon {:class "plus"}]
            [:span (lang/translate :boards :new)]]]
          ]))})))


(def board-page
  "View a single board."
  (r/create-class
   {:component-will-mount #(api/fetch-board-data! (@data/current-board :id) identity)
    :display-name "board-page"
    :reagent-render
    (fn []
      (parts/default-template (str @data/current-board)))}))
