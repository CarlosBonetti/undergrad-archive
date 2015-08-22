# -*- encoding : utf-8 -*-

require 'pg'

module Persistence

  CONNECTION = PG.connect(
    :host => '127.0.0.1',
    :port => 5432,
    :dbname => 'citygame_db',
    :user => 'postgres',
    :password => '1234'
  )

end
