class AddTitleColumnToCutout < ActiveRecord::Migration
  def change
  	add_column :cutouts, :title, :string
  end
end
