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
       [:button.text-blue-500.hover:text-blue-800 {:type "submit"}
        "Sign out"])
      "."]
     [:.h-6]
     [:div "Nothing here yet."])))

(def plugin
  {:routes ["/app" {:middleware [mid/wrap-signed-in]}
            ["" {:get app}]]})




