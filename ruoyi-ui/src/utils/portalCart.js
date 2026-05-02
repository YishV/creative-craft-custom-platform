const CART_KEY = 'creative_portal_cart'
const ADDRESS_KEY = 'creative_portal_address'

export function getCartItems() {
  try {
    return JSON.parse(localStorage.getItem(CART_KEY) || '[]')
  } catch (e) {
    return []
  }
}

export function saveCartItems(items) {
  localStorage.setItem(CART_KEY, JSON.stringify(items || []))
}

export function addCartItem(product, quantity = 1) {
  const items = getCartItems()
  const existing = items.find(item => item.productId === product.productId)
  if (existing) {
    existing.quantity += quantity
  } else {
    items.push({
      productId: product.productId,
      productName: product.productName,
      productType: product.productType,
      creatorId: product.creatorId,
      creatorName: product.creatorName,
      storeName: product.storeName,
      price: Number(product.price || 0),
      remark: product.remark,
      quantity
    })
  }
  saveCartItems(items)
  return items
}

export function removeCartItem(productId) {
  const items = getCartItems().filter(item => item.productId !== productId)
  saveCartItems(items)
  return items
}

export function clearCartItems() {
  saveCartItems([])
}

export function getSavedAddress() {
  try {
    return JSON.parse(localStorage.getItem(ADDRESS_KEY) || '{}')
  } catch (e) {
    return {}
  }
}

export function saveAddress(address) {
  localStorage.setItem(ADDRESS_KEY, JSON.stringify(address || {}))
}
