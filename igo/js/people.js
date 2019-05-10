/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

function getPerson (id) {
  return window.data.people.find(p => p.id === id)
}

function personPic (url) {
  return url.startsWith('data')
    ? url
    : `assets/people/${url}`
}

function updatePeople (screen) {
  const people = window.data.people.sort(distanceSort)
  const content = screen.querySelector('.content')
  content.innerHTML = ''

  for (const { name, id, distance, picture } of people) {
    let el = getListTemplate(screen)

    el.dataset.args = id
    el.querySelector('.picture').src = personPic(picture)
    el.querySelector('.name').innerHTML = name
    el.querySelector('.distance').innerHTML = distance

    content.appendChild(el)
  }

  enableGoto(content)
}

function updatePersonDetails (screen, id) {
  const friend = window.data.people.find(p => p.id === id)
  updateScreenName(friend.name)

  screen.querySelector('.picture').src = personPic(friend.picture)
  screen.querySelector('.name').innerHTML = friend.name
  screen.querySelector('.phone').innerHTML = friend.phone
  screen.querySelector('.distance').innerHTML = friend.distance + 'm'
  screen.querySelector('.msg-btn').dataset.args = id
  screen.querySelector('.call-btn').dataset.args = id
  screen.querySelector('.person-map-btn').dataset.args = id
}

function fillMessages (screen, id) {
  const person = getPerson(id)
  screen.querySelector('input').value = ''
  const content = screen.querySelector('.content')
  content.innerHTML = ''

  for (const { message, from } of person.messages) {
    let el = getListTemplate(screen)

    if (from) el.classList.add('from')

    el.querySelector('.picture').src = `assets/people/${person.picture}`
    el.querySelector('.message').innerHTML = message

    content.appendChild(el)
  }

  content.scrollTo(0, content.scrollHeight)
  screen.querySelector('.input-with-icon span').dataset.args = id

  onMessageInput(screen.querySelector('.input-with-icon input'))
}

function replyMessage (el) {
  const id = el.dataset.args
  const input = el.parentElement.querySelector('input')
  const screen = el.parentElement.parentElement

  const person = getPerson(id)
  person.messages.push({ message: input.value })

  fillMessages(screen, id)
}

function validateNewPerson () {
  const name = document.getElementById('new-person-name').value
  const number = document.getElementById('new-person-phone').value

  if (name.length > 0 && number > 0) {
    return { name, number }
  }
}

function onNewPersonChange () {
  if (validateNewPerson()) {
    document.getElementById('new-person-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-person-submit').classList.add('disabled')
  }
}

function createPerson () {
  const { name, number } = validateNewPerson()

  confirmationBox({
    question: 'Confirma a criação de um novo contacto?',
    rightHandler: () => {
      data.people.push({
        id: uuidv4(),
        name: name,
        distance: Math.floor(Math.random() * 1000) + 1,
        picture: generateAvatar(),
        phone: numberWithSpaces(number),
        lastSeen: Math.floor(Math.random() * 60) + 1,
        messages: []
      })

      runAndBack(clearNewPerson)
    }
  })
}

function callContact (el) {
  const person = getPerson(el.dataset.args)

  confirmationBox({
    question: `Deseja ligar a ${person.name}?`,
    rightHandler: () => {
      showScreen('call', el)
    }
  })
}

function fillCall (screen, id) {
  const person = getPerson(id)

  screen.querySelector('.picture').src = `assets/people/${person.picture}`
  screen.querySelector('.name').innerHTML = person.name

  const end = () => {
    confirmationBox({
      question: 'Deseja terminar a chamada?',
      rightHandler: () => {
        document.querySelector('[data-to=--back]').removeEventListener('click', end)
        screen.querySelector('button').removeEventListener('click', end)
        runAndBack()
      }
    })
  }

  document.querySelector('[data-to=--back]').addEventListener('click', end)
  screen.querySelector('button').addEventListener('click', end)
}

function onMessageInput (el) {
  const div = el.parentElement
  if (div.querySelector('input').value !== '') {
    div.querySelector('span').classList.remove('disabled')
  } else {
    div.querySelector('span').classList.add('disabled')
  }
}

function clearNewPerson () {
  document.getElementById('new-person-submit').classList.add('disabled')
  document.getElementById('new-person-name').value = ''
  document.getElementById('new-person-phone').value = ''
}

function bootstrapPeople () {
  enableKeybaordFor(document.getElementById('msg-kb'))
  enableKeybaordFor(document.getElementById('new-person-name'))
  enableKeybaordFor(document.getElementById('new-person-phone'), false)
}

function seePersonInMap (el) {
  console.warn('TODO: see person in map')
}
