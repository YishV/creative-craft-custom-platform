import Cookies from 'js-cookie'

const AUTH_TYPE_KEY = 'Creative-Auth-Type'
const DEFAULT_AUTH_TYPE = 'admin'
const TOKEN_KEYS = {
  admin: 'Admin-Token',
  buyer: 'Buyer-Token',
  creator: 'Creator-Token'
}

function normalizeAuthType(authType) {
  return TOKEN_KEYS[authType] ? authType : DEFAULT_AUTH_TYPE
}

function inferAuthType(path) {
  if (path === '/buyer/login' || path.indexOf('/buyer/') === 0 || path.indexOf('/portal') === 0) {
    return 'buyer'
  }
  if (path === '/creator/login' || path.indexOf('/creator/') === 0 || path.indexOf('/creative') === 0) {
    return 'creator'
  }
  return DEFAULT_AUTH_TYPE
}

export function getAuthTypeByPath(path) {
  return inferAuthType(path || (window.location && window.location.pathname) || '/')
}

export function setAuthType(authType) {
  const type = normalizeAuthType(authType)
  sessionStorage.setItem(AUTH_TYPE_KEY, type)
  return type
}

export function getAuthType() {
  return normalizeAuthType(sessionStorage.getItem(AUTH_TYPE_KEY) || getAuthTypeByPath())
}

export function ensureAuthType(path) {
  const explicitType = path === '/login'
    ? 'admin'
    : path === '/buyer/login'
      ? 'buyer'
      : path === '/creator/login'
        ? 'creator'
        : ''
  if (explicitType) {
    return setAuthType(explicitType)
  }
  if (!sessionStorage.getItem(AUTH_TYPE_KEY)) {
    return setAuthType(getAuthTypeByPath(path))
  }
  return getAuthType()
}

function getTokenKey(authType) {
  return TOKEN_KEYS[normalizeAuthType(authType)]
}

export function getToken(authType = getAuthType()) {
  return Cookies.get(getTokenKey(authType))
}

export function setToken(token, authType = getAuthType()) {
  return Cookies.set(getTokenKey(authType), token)
}

export function removeToken(authType = getAuthType()) {
  return Cookies.remove(getTokenKey(authType))
}
