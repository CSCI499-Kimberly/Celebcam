class AddProductIdColumnToCutouts < ActiveRecord::Migration
  def change
  	add_column :cutouts, :product_id, :integer
  end
end
