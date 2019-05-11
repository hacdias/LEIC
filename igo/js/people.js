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
  screen.querySelectorAll('button').forEach(btn => { btn.dataset.args = id })
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

function deletePerson (el) {
  const person = getPerson(el.dataset.args)

  confirmationBox({
    question: `Tem a certeza que deseja apagar o contacto "${person.name}"?`,
    rightClass: 'cancel',
    rightHandler: () => {
      const index = data.people.findIndex(el => el.id === person.id)
      data.people.splice(index, 1)
      runAndBack()
    }
  })
}

function callContact (el) {
  console.log(el)
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
        runAndBack()
      }
    })
  }

  document.querySelector('[data-to=--back]').addEventListener('click', end)
  screen.querySelector('button').addEventListener('click', end)
}

function receiveCall (screen, id) {
  const person = getPerson(id)

  screen.querySelector('.picture').src = `assets/people/${person.picture}`
  screen.querySelector('.name').innerHTML = person.name

  const accept = () => {
    let el = document.createElement('x')
    el.dataset.args = person.id
    showScreen('call', el)
  }

  screen.querySelector('.ok').addEventListener('click', accept)

  const end = () => {
    confirmationBox({
      question: 'Deseja rejeitar a chamada?',
      rightHandler: () => {
        document.querySelector('[data-to=--back]').removeEventListener('click', end)
        screen.querySelector('button').removeEventListener('click', end)
        runAndBack()
      }
    })
  }

  document.querySelector('[data-to=--back]').addEventListener('click', end)
  screen.querySelector('.cancel').addEventListener('click', end)
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
  const person = getPerson(el.dataset.args)

  confirmationBox({
    question: `Deseja ir ter com: ${person.name}?`,
    rightHandler: () => {
      showScreen('gps-path', el)
    }
  })
}

function simulateCall (form) {
  let person = getContactFromForm(form)
  console.log(person)
  console.log(person.id)
  let el = document.createElement('x')
  el.dataset.args = person.id
  showScreen('calling', el)

}

function simulateMessage (form) {
  let person = getContactFromForm(form)
  let text = form.querySelector('input[type="text"]').value

  person.messages.push({
    from: true,
    message: text
  })

  let messages = document.getElementById('messages')

  if (messages.className == 'screen active') {
    fillMessages(messages, person.id)
  }

  console.warn('TODO: show badge on messages')
}
