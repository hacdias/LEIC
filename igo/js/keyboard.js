/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

function keyboard () {
  const result = document.querySelector('#keyboard .result')

  var pause = false
  var lPress = false
  var next = false
  var pauseTime = 600
  var longTime = 700
  var lPressTimeout, pauseTimeout
  var lastBtnPressed
  var count = -1
  var numPads = {
    0: [],
    1: ['.', ',', '!'],
    2: ['a', 'b', 'c'],
    3: ['d', 'e', 'f'],
    4: ['g', 'h', 'i'],
    5: ['j', 'k', 'l'],
    6: ['m', 'n', 'o'],
    7: ['p', 'q', 'r', 's'],
    8: ['t', 'u', 'v'],
    9: ['w', 'x', 'y', 'z'],
    '*': [],
    '#': []
  }

  document.querySelectorAll('#keyboard .key').forEach(el => {
    el.addEventListener('mousedown', (event) => {
      event.preventDefault()
      lPressTimeout = window.setTimeout(function () {
        // This timeout is a check for long press;
        lPress = true
        next = true
      }, longTime)

      if (pauseTimeout) {
        window.clearTimeout(pauseTimeout)
      }

      if (pause) {
        count = -1
      }

      // var buttonPressed = event.currentTarget.dataset.value
    })

    el.addEventListener('mouseup', (event) => {
      event.preventDefault()
      count++
      window.clearTimeout(lPressTimeout)
      pauseTimeout = window.setTimeout(function () {
        // This timeout is a check for small pauses for starting the letter from start
        pause = true
        next = true
      }, pauseTime)

      var buttonPressed = event.currentTarget.dataset.value

      result.value = t9(result.value, buttonPressed)
      lastBtnPressed = buttonPressed
    })
  })

  document.querySelector('#keyboard .backspace').addEventListener('mouseup', event => {
    result.value = result.value.substr(0, result.value.length - 1)
  })

  document.querySelector('#keyboard .space').addEventListener('mouseup', event => {
    result.value = result.value + ' '
  })

  function t9 (text, buttonPressed) {
    var mText = text
    var letter
    if (lPress) {
      count = -1
      letter = buttonPressed
    } else {
      if (lastBtnPressed) {
        count = lastBtnPressed === buttonPressed ? count : 0
      }
      if (numPads[buttonPressed].length > 0) {
        count = count >= numPads[buttonPressed].length ? 0 : count
        letter = numPads[buttonPressed][count]
      } else {
        letter = buttonPressed
      }
    }
    letter = '' + letter
    mText += letter

    if (!next && lastBtnPressed === buttonPressed && !letter.match(/[#\*0]/)) {
      mText = mText.substr(0, mText.length - 2) + letter
    }

    if (!lPress) {
      next = false
    }
    pause = false
    lPress = false

    return mText
  }
}

keyboard()

function enableKeybaordFor (element) {
  let kb = document.querySelector('#keyboard')
  let enter = kb.querySelector('.enter')
  let result = kb.querySelector('.result')

  element.addEventListener('click', () => {
    kb.classList.add('active')
    result.value = element.value

    let leave = () => {
      kb.classList.remove('active')
      element.value = result.value

      var evt = document.createEvent('HTMLEvents')
      evt.initEvent('input', false, true)
      element.dispatchEvent(evt)

      result.value = ''
      enter.removeEventListener('mouseup', leave)
    }

    enter.addEventListener('mouseup', leave)
  })
}

// TODO: number keyboard
