(ns slink.db.models.links
  (:require [toucan.models :refer :all]
            [clj-time.coerce :as c]))

(add-type! :clj-time
                  :in identity
                  :out c/from-sql-time)

(defmodel Links :links
  IModel
  (types [this]
                {:created-at :clj-time}))
