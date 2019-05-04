/* eslint-disable no-unused-vars */

function setupClock () {
  const norm = i => i < 10 ? `0${i}` : i
  const today = new Date()
  const hour = norm(today.getHours())
  const mins = norm(today.getMinutes())
  const clocks = document.querySelectorAll('.clock')

  for (const clock of clocks) {
    clock.innerHTML = `${hour}:${mins}`
  }

  setTimeout(setupClock, (60 - today.getSeconds()) * 1000)
}

function uuidv4 () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = Math.random() * 16 | 0; var v = c == 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

function numberWithSpaces (x) {
  var parts = x.toString().split('.')
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ' ')
  return parts.join('.')
}

function updateScreenName (name) {
  document.querySelector('#current-screen-name').innerHTML = name
}

function getListTemplate (screen) {
  let tpl = screen.querySelector('template')
  let clone = document.importNode(tpl.content, true)
  return clone.querySelector('.tpl')
}

function enableGoto (element = document) {
  element.querySelectorAll('.goto').forEach(el => {
    el.addEventListener('click', event => {
      event.preventDefault()

      if (el.dataset.needsunlock) {
        if (!history.find(el => el.name === 'mainmenu')) return
      }

      showScreen(el.dataset.to, el)
    })
  })
}

function confirmationBox ({
  question,
  leftMsg = 'Não',
  rightMsg = 'Sim',
  leftClass = '',
  rightClass = 'ok',
  leftHandler,
  rightHandler,
  anywaysHandler
}) {
  const overlay = document.getElementById('overlay')
  const box = document.getElementById('confirmation-box')
  const leftButton = box.querySelector('#left-confirm')
  const rightButton = box.querySelector('#right-confirm')

  overlay.classList.add('active')
  box.classList.add('active')

  box.querySelector('p').innerHTML = question
  leftButton.innerHTML = leftMsg
  rightButton.innerHTML = rightMsg
  leftButton.className = leftClass
  rightButton.className = rightClass

  const reset = () => {
    leftButton.removeEventListener('click', leftAction)
    rightButton.removeEventListener('click', rightAction)
    overlay.classList.remove('active')
    box.classList.remove('active')
  }

  const leftAction = () => {
    if (leftHandler) leftHandler()
    if (anywaysHandler) anywaysHandler()
    reset()
  }

  const rightAction = () => {
    if (rightHandler) rightHandler()
    if (anywaysHandler) anywaysHandler()
    reset()
  }

  leftButton.addEventListener('click', leftAction)
  rightButton.addEventListener('click', rightAction)
}
