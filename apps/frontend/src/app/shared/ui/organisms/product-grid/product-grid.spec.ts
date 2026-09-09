import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Product } from '../../../../features/catalog/data-access/product.model';
import { ProductCard } from '../product-card/product-card';
import { ProductGrid } from './product-grid';

describe('ProductGrid', () => {
  let fixture: ComponentFixture<ProductGrid>;

  const products: Product[] = [
    { id: 'p1', name: 'Producto 1', description: 'd', unitPrice: 50, category: 'TECNOLOGIA', stock: 3 },
    { id: 'p2', name: 'Producto 2', description: 'd', unitPrice: 20, category: 'OTRO', stock: 1 },
  ];

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductGrid);
    fixture.componentRef.setInput('products', products);
  });

  it('should render one product-card per product, resolving each cart quantity from the map', () => {
    fixture.componentRef.setInput('cartQuantities', new Map([['p1', 2]]));
    fixture.detectChanges();

    const cards = fixture.debugElement.queryAll(By.directive(ProductCard));
    expect(cards.length).toBe(2);

    const card1 = cards.find((card) => (card.componentInstance as ProductCard).product().id === 'p1');
    const card2 = cards.find((card) => (card.componentInstance as ProductCard).product().id === 'p2');

    expect((card1?.componentInstance as ProductCard).quantity()).toBe(2);
    expect((card2?.componentInstance as ProductCard).quantity()).toBe(0);
  });

  it('should forward increment/decrement events with the corresponding productId', () => {
    fixture.componentRef.setInput('cartQuantities', new Map());
    fixture.detectChanges();

    const incrementSpy = vi.fn();
    const decrementSpy = vi.fn();
    fixture.componentInstance.increment.subscribe(incrementSpy);
    fixture.componentInstance.decrement.subscribe(decrementSpy);

    const cards = fixture.debugElement.queryAll(By.directive(ProductCard));
    const secondCard = cards[1].componentInstance as ProductCard;
    secondCard.increment.emit();
    secondCard.decrement.emit();

    expect(incrementSpy).toHaveBeenCalledWith('p2');
    expect(decrementSpy).toHaveBeenCalledWith('p2');
  });
});
