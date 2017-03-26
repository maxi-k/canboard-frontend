(ns canboard-frontend.view
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang :refer [translate]]
            [canboard-frontend.api :as api]
            [canboard-frontend.util :as util]
            [canboard-frontend.route :as route]
            [canboard-frontend.view.boards :as boards]
            [canboard-frontend.view.parts :as parts :refer [default-template]]))

(defn home-page []
  (default-template
   [:div
    [:h2 (parts/title)]
    [:h2 "Welcome"]
    [:div [:p] [:h4 "User Data"]
     [:p
      (str @data/current-user)]]]))

(defn boards-page [] boards/overview)
(defn board-page [] boards/board-page)

(def login-page
  (letfn [(auth []
            (api/authenticate-user! (.-value (util/elem-by-id :login-username))
                                    (.-value (util/elem-by-id :login-password))
                                    (fn []
                                      (when (= "/" (util/current-path))
                                        (route/goto! (route/boards-route))))))
          (key-callback [e] (when (= 13 (.-charCode e)) (auth)))
          (btn-callback [e] (auth))]
    (fn []
      [:div {:class "ui grid centered equal width" :id :login-segment}
       [sa/Segment {:class "twelve"}
        [sa/Image {:size :small
                   :class "ui centered"
                   :src "/img/logo/logo-large.png"}]
        [:div#big-title (parts/title)]
        (when (:unauthorized @data/current-user)
          [:div#alert {:class "ui yellow segment"}
           [:span (lang/translate :session-over) "."] [:br]
           [:span (lang/translate :login-again) "."]])
        [:div.ui.form
         [:div.field
          [:label (translate :user-name)]
          [:input {:id :login-username :type :text :name :user-name :placeholder "test@example.com"
                   :on-key-press key-callback}]]
         [:div.field
          [:label (translate :password)]
          [:input {:id :login-password :type :password :name :password :placeholder "password"
                   :on-key-press key-callback}]]
         [sa/Button {:on-click btn-callback}
          (translate :do-login)]]]])))
