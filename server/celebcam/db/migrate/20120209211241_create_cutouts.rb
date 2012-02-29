class CreateCutouts < ActiveRecord::Migration
  def change
    create_table :cutouts do |t|
      t.string :image_link

      t.timestamps
    end
  end
end
