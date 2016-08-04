(ns cljs-boot-starter.client
  (:require [reagent.core :as ra :refer [atom render]]))

(enable-console-print!)

(defn hello []
  [:div
   "Hello world!"])

(defn init []
  (render [hello] (.getElementById js/document "my-app-area")))

(init)
