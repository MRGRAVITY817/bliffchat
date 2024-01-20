(ns com.bliffchat.app
  (:require [com.biffweb :as biff :refer [q]]
            [com.bliffchat.middleware :as mid]
            [com.bliffchat.ui :as ui]
            [xtdb.api :as xt]))

(defn app [{:keys [session biff/db] :as ctx}]
  (let [{:user/keys [email]} (xt/entity db (:uid session))]
    (ui/page
     {}
     [:div "Signed in as " email ". "
      (biff/form
       {:action "/auth/signout"
        :class "inline"}
       [:button.text-blue-500.hover:text-blue-800
        {:type "submit"}
        "Sign out"])
      "."]
     [:.h-6]
     (biff/form
      {:action "/community"}
      [:button.btn {:type "submit"} "New community"]))))

(defn new-community [{:keys [session] :as ctx}]
  (let [comm-id (random-uuid)]
    (biff/submit-tx
     ctx
     [{:db/doc-type  :community
       :xt/id        comm-id
       :comm/title   (str "Community #" (rand-int 1000))}
      {:db/doc-type  :membership
       :mem/user     (:uid session)
       :mem/comm     comm-id
       :mem/roles    #{:admin}}])
    {:status 303
     :headers {"Location" (str "/community/" comm-id)}}))

(defn community [{:keys [biff/db path-params] :as ctx}]
  (if-some [comm (xt/entity db (parse-uuid (:id path-params)))]
    (ui/page {}
             [:p "Welcome to " (:comm/title comm)])
    {:status 303
     :headers {"location" "/app"}}))

(def plugin
  {:routes ["" {:middleware [mid/wrap-signed-in]}
            ["/app"           {:get app}]
            ["/community"     {:post new-community}]
            ["/community/:id" {:get community}]]})




