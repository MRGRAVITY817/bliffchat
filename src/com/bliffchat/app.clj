(ns com.bliffchat.app
  (:require [com.biffweb :as biff :refer [q]]
            [com.bliffchat.middleware :as mid]
            [com.bliffchat.ui :as ui]
            [xtdb.api :as xt]))

(defn app [ctx]
  (ui/app-page
   ctx
   [:p "Select a community, or create a new one."]))

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
  (if (some? (xt/entity db (parse-uuid (:id path-params))))
    (ui/app-page
     ctx
     [:.border.border-neutral-600.p-3.bg-white.grow
      "Message window"]
     [:.h-3]
     [:.border.border-neutral-600.p-3.h-28.bg-white
      "Compose window"])
    {:status 303
     :headers {"location" "/app"}}))

(def plugin
  {:routes ["" {:middleware [mid/wrap-signed-in]}
            ["/app"           {:get app}]
            ["/community"     {:post new-community}]
            ["/community/:id" {:get community}]]})




