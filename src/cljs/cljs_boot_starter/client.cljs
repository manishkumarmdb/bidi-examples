(ns cljs-boot-starter.client
  (:require [reagent.core :as reagent :refer [atom render]]
            [reagent.session :as session]
            [bidi.bidi :as bidi]
            [accountant.core :as accountant]))

(enable-console-print!)

(defonce index1-pages ["home" "about" "work" "career" "contact"])

(def app-routes
  ["/"
   [["" :index]
    ["index-1"
     [["" :index-1]
      [["/page-" :page] :index1-1]]]
    ["index-2" :index-2]
    ["missing-route" :missing-route]
    [true :page-not-found]
    ]])

(defmulti page-contents identity)

(defmethod page-contents :index []
  [:span
   [:h2 "bidi/accountant example: Index"]
   [:ul
    [:li [:a {:href (bidi/path-for app-routes :index-1) } "Index-I"]]
    [:li [:a {:href (bidi/path-for app-routes :index-2) } "Index-II"]]
    [:li [:a {:href (bidi/path-for app-routes :missing-route) } "Missing-Route"]]
    [:li [:a {:href "/page/!found" } "Other link"]]
    ]])

(defmethod page-contents :index-1 []
  [:span
   [:h3 "Bidi Routing example: Index-I"]
   [:ul (map (fn [item-id]
               [:li {:key (str "item-" item-id)}
                [:a {:href (bidi/path-for app-routes :index1-1 :page item-id)} (str item-id)]])
             index1-pages)]])


(defmethod page-contents :index1-1 []
  (let [routing-data (session/get :route)
        index1-page (get-in routing-data [:route-params :page])]
    [:span
     [:h3 (str "Bidi Routing example: Index-I, you'r @ : " index1-page " page")]
     [:p [:a {:href (bidi/path-for app-routes :index-1)} "Back to Index-I"]]]))

(defmethod page-contents :index-2 []
  [:span
   [:h1 "Routing example: Index-II"]])

(defmethod page-contents :page-not-found []
  "non existing routes go here"
  [:span
   [:h2
    "Bidi Routing examole: Other link"]
   [:hr]
   [:h3 "404: couldn't find this page"]
   [:pre.verse
    "what you are looking for, i don't have"]])

(defmethod page-contents :default []
  "Configured routes, missing an implementation, go here"
  [:span
   [:h2 "404: My bad"]
   [:pre.verse
    "This page should be here, but I never created it."]])


(defn page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:p [:a {:href (bidi/path-for app-routes :index) } "home"]]
       [:hr]
       (page-contents page)
       [:hr]
       [:p "(Using "
        [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", "
        [:a {:href "https://github.com/juxt/bidi"} "Bidi"] " & "
        [:a {:href "https://github.com/venantius/accountant"} "Accountant"]
        ")"]])))

(defn init []
  (render [page] (.getElementById js/document "my-app-area")))

(defn ^:export init! []
  (accountant/configure-navigation!
   {:nav-handler (fn
                   [path]
                   (let [match (bidi/match-route app-routes path)
                         current-page (:handler match)
                         route-params (:route-params match)]
                     (session/put! :route {:current-page current-page
                                           :route-params route-params})))
    :path-exists? (fn [path]
                    (boolean (bidi/match-route app-routes path)))})
  (accountant/dispatch-current!)
  (init))

(init!)
