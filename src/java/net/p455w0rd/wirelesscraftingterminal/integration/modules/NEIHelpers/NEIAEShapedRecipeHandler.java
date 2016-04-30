package net.p455w0rd.wirelesscraftingterminal.integration.modules.NEIHelpers;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.p455w0rd.wirelesscraftingterminal.api.exceptions.MissingIngredientError;
import net.p455w0rd.wirelesscraftingterminal.api.exceptions.RegistrationError;
import net.p455w0rd.wirelesscraftingterminal.api.recipes.IIngredient;
import net.p455w0rd.wirelesscraftingterminal.api.recipes.game.ShapedRecipe;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;

import appeng.core.AEConfig;
import appeng.util.Platform;


public class NEIAEShapedRecipeHandler extends TemplateRecipeHandler
{

	@Override
	public void loadTransferRects()
	{
		this.transferRects.add( new TemplateRecipeHandler.RecipeTransferRect( new Rectangle( 84, 23, 24, 18 ), "crafting" ) );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes( final String outputId, final Object... results )
	{
		if( ( outputId.equals( "crafting" ) ) && ( this.getClass() == NEIAEShapedRecipeHandler.class ) )
		{
			final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
			for( final IRecipe recipe : recipes )
			{
				if( ( recipe instanceof ShapedRecipe ) )
				{
					if( ( (ShapedRecipe) recipe ).isEnabled() )
					{
						final CachedShapedRecipe cachedRecipe = new CachedShapedRecipe( (ShapedRecipe) recipe );
						cachedRecipe.computeVisuals();
						this.arecipes.add( cachedRecipe );
					}
				}
			}
		}
		else
		{
			super.loadCraftingRecipes( outputId, results );
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadCraftingRecipes( final ItemStack result )
	{
		final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		for( final IRecipe recipe : recipes )
		{
			if( ( recipe instanceof ShapedRecipe ) )
			{
				if( ( (ShapedRecipe) recipe ).isEnabled() && NEIServerUtils.areStacksSameTypeCrafting( recipe.getRecipeOutput(), result ) )
				{
					final CachedShapedRecipe cachedRecipe = new CachedShapedRecipe( (ShapedRecipe) recipe );
					cachedRecipe.computeVisuals();
					this.arecipes.add( cachedRecipe );
				}
			}
		}
	}
/*
	@Override
	public void loadUsageRecipes( final ItemStack ingredient )
	{
		final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		for( final IRecipe recipe : recipes )
		{
			if( ( recipe instanceof ShapedRecipe ) )
			{
				final CachedShapedRecipe cachedRecipe = new CachedShapedRecipe( (ShapedRecipe) recipe );

				if( ( (ShapedRecipe) recipe ).isEnabled() && cachedRecipe.contains( cachedRecipe.ingredients, ingredient.getItem() ) )
				{
					cachedRecipe.computeVisuals();
					if( cachedRecipe.contains( cachedRecipe.ingredients, ingredient ) )
					{
						cachedRecipe.setIngredientPermutation( cachedRecipe.ingredients, ingredient );
						this.arecipes.add( cachedRecipe );
					}
				}
			}
		}
	}
*/
	@Override
	public String getGuiTexture()
	{
		return "textures/gui/container/crafting_table.png";
	}

	@Override
	public String getOverlayIdentifier()
	{
		return "crafting";
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiCrafting.class;
	}

	@Override
	public boolean hasOverlay( final GuiContainer gui, final Container container, final int recipe )
	{
		return ( super.hasOverlay( gui, container, recipe ) ) || ( ( this.isRecipe2x2( recipe ) ) && ( RecipeInfo.hasDefaultOverlay( gui, "crafting2x2" ) ) );
	}

	@Override
	public IRecipeOverlayRenderer getOverlayRenderer( final GuiContainer gui, final int recipe )
	{
		final IRecipeOverlayRenderer renderer = super.getOverlayRenderer( gui, recipe );
		if( renderer != null )
		{
			return renderer;
		}

		final IStackPositioner positioner = RecipeInfo.getStackPositioner( gui, "crafting2x2" );
		if( positioner == null )
		{
			return null;
		}

		return new DefaultOverlayRenderer( this.getIngredientStacks( recipe ), positioner );
	}

	@Override
	public IOverlayHandler getOverlayHandler( final GuiContainer gui, final int recipe )
	{
		final IOverlayHandler handler = super.getOverlayHandler( gui, recipe );
		if( handler != null )
		{
			return handler;
		}

		return RecipeInfo.getOverlayHandler( gui, "crafting2x2" );
	}

	private boolean isRecipe2x2( final int recipe )
	{
		for( final PositionedStack stack : this.getIngredientStacks( recipe ) )
		{
			if( ( stack.relx > 43 ) || ( stack.rely > 24 ) )
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String getRecipeName()
	{
		return NEIClientUtils.translate( "recipe.shaped" );
	}

	private class CachedShapedRecipe extends TemplateRecipeHandler.CachedRecipe
	{

		private final List<PositionedStack> ingredients;
		private final PositionedStack result;

		public CachedShapedRecipe( final ShapedRecipe recipe )
		{
			this.result = new PositionedStack( recipe.getRecipeOutput(), 119, 24 );
			this.ingredients = new ArrayList<PositionedStack>();
			this.setIngredients( recipe.getWidth(), recipe.getHeight(), recipe.getIngredients() );
		}

		private void setIngredients( final int width, final int height, final Object[] items )
		{
			final boolean useSingleItems = AEConfig.instance.disableColoredCableRecipesInNEI();
			for( int x = 0; x < width; x++ )
			{
				for( int y = 0; y < height; y++ )
				{
					if( items[( y * width + x )] != null )
					{
						final IIngredient ing = (IIngredient) items[( y * width + x )];

						try
						{
							final ItemStack[] is = ing.getItemStackSet();
							final PositionedStack stack = new PositionedStack( useSingleItems ? Platform.findPreferred( is ) : is, 25 + x * 18, 6 + y * 18, false );
							stack.setMaxSize( 1 );
							this.ingredients.add( stack );
						}
						catch( final RegistrationError ignored )
						{

						}
						catch( final MissingIngredientError ignored )
						{

						}
					}
				}
			}
		}

		@Override
		public PositionedStack getResult()
		{
			return this.result;
		}

		@Override
		public List<PositionedStack> getIngredients()
		{
			return this.getCycledIngredients( NEIAEShapedRecipeHandler.this.cycleticks / 20, this.ingredients );
		}

		private void computeVisuals()
		{
			for( final PositionedStack p : this.ingredients )
			{
				p.generatePermutations();
			}
			this.result.generatePermutations();
		}
	}
}